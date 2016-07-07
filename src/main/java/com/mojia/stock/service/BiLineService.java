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

    public List<BiLineDo> parseBiLine(List<KBarDo> kBars) {
        if (CollectionUtils.isEmpty(kBars)) {
            return Collections.emptyList();
        }

        //包含处理
        containsProcess(kBars);

        //顶底分型标记
        List<KBarDo> partingList = markingParting(kBars);

        //笔构造
        List<BiLineDo> biLines = buildChanBiLine(partingList, kBars);

        return biLines;
    }

    private List<BiLineDo> buildChanBiLine(List<KBarDo> partingList, List<KBarDo> kBars) {
        List<BiLineDo> biLines = new ArrayList<BiLineDo>();

        KBarDo start = kBars.get(0);

        while (true) {
            KBarDo possibleLineEnd = findPossibleEndBi(start, partingList, kBars);
            if (possibleLineEnd == null) {
                break;
            }

            KBarDo possibleNextLineEnd = findPossibleEndBi(possibleLineEnd, partingList, kBars);
            //从前面一笔,继续往后追溯
            while (possibleNextLineEnd == null) {
                possibleLineEnd = continueFindPossibleEndBi(start, possibleLineEnd, partingList, kBars);

                if (possibleLineEnd == null) {
                    possibleLineEnd = useLastParting(start, partingList);

                    biLines.add(buildChanBi(start, possibleLineEnd, kBars));

                    return biLines;
                }

                possibleNextLineEnd = findPossibleEndBi(possibleLineEnd, partingList, kBars);
            }

            while (isOneDirection(possibleLineEnd, possibleNextLineEnd)) {
                possibleNextLineEnd = findPossibleEndBi(possibleNextLineEnd, partingList, kBars);
                if (possibleNextLineEnd == null) {
                    break;
                }
            }

            biLines.add(buildChanBi(start, possibleLineEnd, kBars));

            start = possibleLineEnd;
        }

        return biLines;
    }

    //continue find a new chan bi line
    private KBarDo continueFindPossibleEndBi(KBarDo start, KBarDo skip, List<KBarDo> partingList, List<KBarDo> kBars) {
        for (int i = 0; i < partingList.size(); i++) {
            KBarDo currentPartingPoint = partingList.get(i);

            if (skip.getDate().compareTo(currentPartingPoint.getDate()) >= 0) {
                continue;
            }

            if (possibleBuildChanBi(kBars, start, currentPartingPoint)) {
                return currentPartingPoint;
            }
        }

        return null;
    }

    private KBarDo useLastParting(KBarDo start, List<KBarDo> partingList) {
        KBarDo end = null;

        if (start.isBottomPoint()) {
            if (partingList.get(partingList.size() - 1).isPeakPoint()) {
                end = partingList.get(partingList.size() - 1);
            } else {
                end = partingList.get(partingList.size() - 2);
            }
        }

        if (start.isPeakPoint()) {
            if (partingList.get(partingList.size() - 1).isBottomPoint()) {
                end = partingList.get(partingList.size() - 1);
            } else {
                end = partingList.get(partingList.size() - 2);
            }
        }

        return end;
    }

    private BiLineDo buildChanBi(KBarDo start, KBarDo end, List<KBarDo> kBars) {
        BiLineDo biLineDo = new BiLineDo();

        biLineDo.setStart(start);
        biLineDo.setEnd(end);

        int startIndex = kBars.indexOf(start);
        int endIndex = kBars.indexOf(end);
        biLineDo.setkBarDoList(kBars.subList(startIndex, endIndex + 1));

        biLineDo.setUp(end.isPeakPoint());

        return biLineDo;
    }

    private boolean isOneDirection(KBarDo possibleEnd, KBarDo possibleSecondEnd) {
        if ((possibleEnd.isPeakPoint() && possibleSecondEnd.isPeakPoint()) ||
                (possibleEnd.isBottomPoint() && possibleSecondEnd.isBottomPoint())) {
            return true;
        }

        return false;
    }

    private KBarDo findPossibleEndBi(KBarDo start, List<KBarDo> partingList, List<KBarDo> kBars) {
        KBarDo current = skipToCurrent(partingList, start);

        int i = partingList.indexOf(current);
        while (i < partingList.size()) {
            current = partingList.get(i);

            if (possibleBuildChanBi(kBars, start, current)) {

                if (i == partingList.size() - 1) {
                    break;
                }

                KBarDo next = partingList.get(i + 1);

                if (possibleBuildChanBi(kBars, current, next)) {
                    return current;
                } else {
                    i++;
                }
            } else {
                i++;
            }
        }

        return null;
    }

    private KBarDo skipToCurrent(List<KBarDo> partingList, KBarDo start) {
        for (int i = 0; i < partingList.size(); i++) {
            KBarDo currentPartingPoint = partingList.get(i);

            if (currentPartingPoint.getDate().compareTo(start.getDate()) > 0) {
                return currentPartingPoint;
            }
        }

        return null;
    }

    //分型标记
    private List<KBarDo> markingParting(List<KBarDo> kBars) {
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

        return partingList;
    }

    private boolean canMergePartings(KBarDo current, KBarDo next, List<KBarDo> kBars) {
        int currentIndex = kBars.indexOf(current);
        int nextIndext = kBars.indexOf(next);

        return nextIndext - currentIndex < 4;
    }

    //两个分型间隔是否超过5根K线
    private boolean possibleBuildChanBi(List<KBarDo> kBarList, KBarDo start, KBarDo end) {
        int startIndex = kBarList.indexOf(start);
        int endIndex = kBarList.indexOf(end);

        if (start == kBarList.get(0)) {
            if (isParting(end) && (endIndex - startIndex > 3)) {

                if ((end.isBottomPoint() && end.getHigh() < start.getLow()) ||
                        (end.isPeakPoint() && end.getLow() > start.getHigh())) {
                    return true;
                }
            }
        } else {

            if (isParting(start) && isParting(end)) {
                if ((!isOneDirection(start, end)) && (endIndex - startIndex > 3)) {

                    if ((end.isBottomPoint() && end.getHigh() < start.getLow()) ||
                            (end.isPeakPoint() && end.getLow() > start.getHigh())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isParting(KBarDo bar) {
        return bar.isPeakPoint() || bar.isBottomPoint();
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
    private void containsProcess(List<KBarDo> kBarDos) {
        for (int i = 1; i < kBarDos.size() - 1; ) {
            if (!containsProcess(kBarDos, i)) {
                i++;
            }
        }
    }

    //包含处理第i个KLine
    private boolean containsProcess(List<KBarDo> kBarDos, int i) {
        KBarDo current = kBarDos.get(i);
        KBarDo next = kBarDos.get(i + 1);

        if (isContains(current, next)) {//Contains
            UpDownRelationType beforeType = UpDownRelationType.UP;
            if (i > 0) {
                beforeType = twoKLineRelation(kBarDos.get(i - 1), current);
            }

            KBarDo newKBar = processContainsToNewKBar(beforeType, current, next);
            if (newKBar == null) {
                return goFrontContainsProcess(kBarDos, i - 1);
            }

            kBarDos.add(i, newKBar);

            kBarDos.remove(i + 1);
            kBarDos.remove(i + 1);

            int beforeIndex = i - 1;
            while (beforeIndex > 0) {
                if (!containsProcess(kBarDos, beforeIndex)) {
                    break;
                } else {
                    beforeIndex--;
                }
            }

            return true;
        }

        return false;
    }

    private boolean goFrontContainsProcess(List<KBarDo> kBarDos, int i) {
        KBarDo current = kBarDos.get(i);
        KBarDo next = kBarDos.get(i + 1);

        if (isContains(current, next)) {//Contains
            UpDownRelationType beforeType = UpDownRelationType.UP;
            if (i > 0) {
                beforeType = twoKLineRelation(kBarDos.get(i - 1), current);
            }

            KBarDo newKBar = processContainsToNewKBar(beforeType, current, next);
            if (newKBar == null) {
                goFrontContainsProcess(kBarDos, i - 1);
            }

            kBarDos.add(i, newKBar);

            kBarDos.remove(i);
            kBarDos.remove(i + 1);

            int beforeIndex = i - 1;
            while (beforeIndex > 0) {
                if (!containsProcess(kBarDos, beforeIndex)) {
                    break;
                } else {
                    beforeIndex--;
                }
            }

            return true;
        }

        return false;
    }

    private boolean isContains(KBarDo current, KBarDo next) {
        UpDownRelationType relationType = twoKLineRelation(current, next);

        return relationType == UpDownRelationType.UPContains || relationType == UpDownRelationType.DownContains;
    }

    private UpDownRelationType twoKLineRelation(KBarDo current, KBarDo next) {
        if (next.getHigh() >= current.getHigh() &&
                next.getLow() <= current.getLow()) {
            return UpDownRelationType.UPContains;
        }

        if (next.getHigh() <= current.getHigh() &&
                next.getLow() >= current.getLow()) {
            return UpDownRelationType.DownContains;
        }

        if (next.getHigh() < current.getHigh() &&
                next.getLow() < current.getLow()) {
            return UpDownRelationType.Down;
        }

        return UpDownRelationType.UP;
    }

    private KBarDo processContainsToNewKBar(UpDownRelationType beforeType, KBarDo current, KBarDo next) {
        if (beforeType == UpDownRelationType.UP) {
            return upDirectionProcess(current, next);
        } else if (beforeType == UpDownRelationType.Down) {
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













