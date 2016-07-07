package com.mojia.stock.domain;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 缠论中一笔
 * Created by wangxin on 16/7/5.
 */
public class BiLineDo {
    private List<KBarDo> kBarDoList;
    private KBarDo start;
    private KBarDo end;
    private boolean isUp;

    public List<KBarDo> getkBarDoList() {
        return kBarDoList;
    }

    public void setkBarDoList(List<KBarDo> kBarDoList) {
        this.kBarDoList = kBarDoList;
    }

    public KBarDo getStart() {
        return start;
    }

    public void setStart(KBarDo start) {
        this.start = start;
    }

    public KBarDo getEnd() {
        return end;
    }

    public void setEnd(KBarDo end) {
        this.end = end;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(new SimpleDateFormat("MMdd").format(start.getDate()));
        sb.append("->");
        sb.append(new SimpleDateFormat("MMdd").format(end.getDate()));
        if (isUp) {
            sb.append("向上笔");
        } else {
            sb.append("向下笔");
        }

        return sb.toString();
    }
}
