package com.mojia.stock.service;

import com.mojia.stock.TimePeriodType;
import com.mojia.stock.UpDownRelationType;
import com.mojia.stock.domain.BiLineDo;
import com.mojia.stock.domain.KBarDo;
import org.apache.activemq.store.kahadaptor.IntegerMarshaller;
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

    public static void main(String[] args) {
        List<Integer> data = new ArrayList<Integer>();
        data.add(0);
        data.add(1);
        data.add(2);
        data.add(3);
        data.add(4);

        for (int i = 0; i < data.size() - 1; i++) {
            if (remove(i, i + 1)) {
                data.add(i, 100);

                data.remove(i + 1);
                data.remove(i + 1);
            }
        }
        System.out.println(data);
    }

    private static boolean remove(int i, int i1) {
        return i == 2;
    }

    public List<BiLineDo> parseBiLine(List<KBarDo> kBars) {
        if (CollectionUtils.isEmpty(kBars)) {
            return Collections.emptyList();
        }

        //预处理
        preProcess(kBars);

        //顶底分型处理
        List<KBarDo> partingList = new ArrayList<KBarDo>();
        for (int i = 1; i < kBars.size() - 1; i++) {
            KBarDo before = kBars.get(i - 1);
            KBarDo current = kBars.get(i);
            KBarDo next = kBars.get(i + 1);

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

            if (possibleBuildChanBi(kBars, current, partingList.get(i + 1))) {
                if (possibleBuildChanBi(kBars, partingList.get(i + 1), partingList.get(i + 2))) {
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
    private void preProcess(List<KBarDo> kBarDos) {
        for (int i = 0; i < kBarDos.size() - 1; ) {
            KBarDo current = kBarDos.get(i);
            KBarDo next = kBarDos.get(i + 1);

            if (isContains(current, next)) {//Contains
                KBarDo before = (i == 0 ? kBarDos.get(i) : kBarDos.get(i - 1));
                KBarDo newKBar = processContainsToNewKBar(before, current, next);

                kBarDos.add(i, newKBar);

                kBarDos.remove(i + 1);
                kBarDos.remove(i + 1);

                if(){

                }
            }
        }
    }

    private boolean isContains(KBarDo current, KBarDo next) {
        UpDownRelationType relationType = twoKLineRelation(current, next);

        return relationType == UpDownRelationType.UPContains || relationType == UpDownRelationType.DownContains;
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

        bar.setHigh(Math.min(current.getHigh(), next.getHigh()));
        bar.setLow(Math.min(current.getLow(), next.getLow()));

        bar.setClose(current.getClose());
        bar.setOpen(current.getOpen());
        bar.setTimePeriodType(current.getTimePeriodType());
        bar.setDate(current.getDate());

        return bar;
    }

    //向上处理
    private KBarDo upDirectionProcess(KBarDo current, KBarDo next) {
        KBarDo bar = new KBarDo();

        bar.setHigh(Math.max(next.getHigh(), current.getHigh()));
        bar.setLow(Math.max(next.getLow(), current.getLow()));
        bar.setClose(current.getClose());
        bar.setAdjClose(current.getAdjClose());
        bar.setOpen(current.getOpen());
        bar.setTimePeriodType(current.getTimePeriodType());
        bar.setDate(current.getDate());

        return bar;
    }

}













