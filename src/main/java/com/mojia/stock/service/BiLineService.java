package com.mojia.stock.service;

import com.mojia.stock.TimePeriodType;
import com.mojia.stock.UpDownRelationType;
import com.mojia.stock.domain.BiLineDo;
import com.mojia.stock.domain.KBarDo;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangxin on 16/7/5.
 */
@Service
public class BiLineService {
    @Resource
    private KBarService kBarService;

    public List<BiLineDo> parseBiLine(List<KBarDo> kBars) {
        if (CollectionUtils.isNotEmpty(kBars)) {
            return Collections.emptyList();
        }

        //预处理
        List<KBarDo> kBarList = preProcess(kBars);

        //顶底分型处理
        List<KBarDo> partingList = new ArrayList<KBarDo>();
        for (int i = 1; i < kBarList.size() - 1; i++) {
            KBarDo before = kBarList.get(i - 1);
            KBarDo current = kBarList.get(i);
            KBarDo next = kBarList.get(i + 1);

            if (isTopParting(before, current, next)) {
                current.setPeakPoint(true);
                partingList.add(current);
            }

            if (isBottomParting(before, current, next)) {
                current.setBottomPoint(true);
                partingList.add(current);
            }
        }

        //笔构造
        int i = 0;
        List<BiLineDo> biLines = new ArrayList<BiLineDo>();
        while (i < partingList.size()) {
            KBarDo current = partingList.get(i);

            if (possibleBuildChanBi(kBarList, current, partingList.get(i + 1))) {
                if (possibleBuildChanBi(kBarList, partingList.get(i + 1), partingList.get(i + 2))) {
                    BiLineDo biLineDo = new BiLineDo();

                    biLineDo.setStart(current);
                    biLineDo.setEnd(partingList.get(i + 1));
                    //
                    biLineDo.setkBarDoList(null);
                    biLineDo.setUp(current.isBottomPoint());

                    biLines.add(biLineDo);
                }
            }
        }

        return biLines;
    }

    //两个分型间隔是否超过5根K线
    private boolean possibleBuildChanBi(List<KBarDo> kBarList, KBarDo current, KBarDo next) {
        int currentIndex = kBarList.indexOf(current);
        int nextIndex = kBarList.indexOf(next);

        return nextIndex - currentIndex > 4;
    }

    private boolean isBottomParting(KBarDo before, KBarDo current, KBarDo next) {
        if (current.getHigh() < before.getHigh() && current.getHigh() < next.getHigh() &&
                current.getLow() < before.getLow() && current.getLow() < next.getLow()) {
            return true;
        }

        return false;
    }

    //缠顶分型判断
    private boolean isTopParting(KBarDo before, KBarDo current, KBarDo next) {
        if (current.getHigh() > before.getHigh() && current.getHigh() > next.getHigh() &&
                current.getLow() > before.getLow() && current.getLow() > next.getLow()) {
            return true;
        }

        return false;
    }


    //包含处理
    private List<KBarDo> preProcess(List<KBarDo> kBarDos) {
        List<KBarDo> chanKBarDos = new ArrayList<KBarDo>();

        for (int i = 0; i < kBarDos.size() - 1; i++) {
            KBarDo current = kBarDos.get(i);
            KBarDo next = kBarDos.get(i + 1);

            UpDownRelationType relationType = twoKLineRelation(current, next);

            if (relationType == UpDownRelationType.UPContains || relationType == UpDownRelationType.DownContains) {//Contains
                KBarDo before = (i == 0 ? kBarDos.get(i) : kBarDos.get(i - 1));
                KBarDo newKBar = processContainsToNewKBar(before, current, next);

                chanKBarDos.add(newKBar);
            } else {
                chanKBarDos.add(current);
            }
        }

        return chanKBarDos;
    }

    private UpDownRelationType twoKLineRelation(KBarDo current, KBarDo next) {
        if (next.getHigh() > current.getHigh() &&
                next.getLow() < current.getLow()) {
            return UpDownRelationType.UPContains;
        }

        if (next.getHigh() < current.getHigh() &&
                next.getLow() > current.getLow()) {
            return UpDownRelationType.DownContains;
        }

        if (next.getHigh() < current.getHigh() &&
                next.getLow() < current.getLow()) {
            return UpDownRelationType.Down;
        }

        return UpDownRelationType.UP;
    }

    private KBarDo processContainsToNewKBar(KBarDo before, KBarDo current, KBarDo next) {
        UpDownRelationType beforeType = twoKLineRelation(before, current);

        if (beforeType == UpDownRelationType.UP) {
            return upDirectionProcess(current, next);
        }

        if (beforeType == UpDownRelationType.Down) {
            return downDirectionProcess(current, next);
        }

        return null;
    }

    //向下处理
    private KBarDo downDirectionProcess(KBarDo current, KBarDo next) {
        KBarDo bar = new KBarDo();

        bar.setHigh(current.getHigh());
        bar.setLow(next.getLow());
        bar.setClose(next.getClose());
        bar.setOpen(next.getOpen());
        bar.setTimePeriodType(next.getTimePeriodType());
        bar.setDate(next.getDate());

        return bar;
    }

    //向上处理
    private KBarDo upDirectionProcess(KBarDo current, KBarDo next) {
        KBarDo bar = new KBarDo();

        bar.setHigh(next.getHigh());
        bar.setLow(current.getLow());
        bar.setClose(next.getClose());
        bar.setOpen(next.getOpen());
        bar.setTimePeriodType(next.getTimePeriodType());
        bar.setDate(next.getDate());

        return bar;
    }

}













