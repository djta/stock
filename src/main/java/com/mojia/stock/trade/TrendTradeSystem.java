package com.mojia.stock.trade;

import com.mojia.stock.common.ActionType;
import com.mojia.stock.domain.ActionDo;
import com.mojia.stock.domain.BiLineDo;
import com.mojia.stock.service.PositionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxin on 16/7/8.
 */
@Service
public class TrendTradeSystem {
    @Resource
    private PositionService positionService;

    public List<ActionDo> trade(List<BiLineDo> biLines) {
        List<ActionDo> actions = new ArrayList<ActionDo>();
        List<TrendModel> trendModels = new ArrayList<TrendModel>();


        int i = 0;
        BiLineDo first = biLines.get(i);
        while (i++ < biLines.size()) {
            BiLineDo current = biLines.get(i);

            if (CollectionUtils.isEmpty(trendModels)) {
                if (mayBeTrend(first, current)) {
                    TrendModel trend = new TrendModel(first.getStart(), current.getEnd());

                    if (confirmTrend(trend, i, biLines)) {
                        actions.add(positionService.action(ActionType.CreatePosition));
                        continue;
                    }
                }

                first = biLines.get(i + 1);
            } else {

            }
        }

        return actions;
    }

    private boolean mayBeTrend(BiLineDo first, BiLineDo second) {
        if (first.isUp() && (!second.isUp())) {

            return (second.getEnd().getLow() >= first.getStart().getLow());
        }

        return false;
    }

    private boolean confirmTrend(TrendModel trend, int i, List<BiLineDo> biLines) {
        BiLineDo fourthBi = biLines.get(i + 2);

        if (fourthBi.getEnd().getLow() >= trend.getSecond().getLow()) {

            return true;
        }

        return false;
    }

}
