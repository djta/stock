package com.mojia.stock.trade;

import com.mojia.stock.domain.KBarDo;

/**
 * Created by wangxin on 16/7/8.
 */
public class TrendModel {
    private KBarDo first;
    private KBarDo second;

    public TrendModel(KBarDo first, KBarDo second) {
        this.first = first;
        this.second = second;
    }

    public KBarDo getFirst() {
        return first;
    }

    public void setFirst(KBarDo first) {
        this.first = first;
    }

    public KBarDo getSecond() {
        return second;
    }

    public void setSecond(KBarDo second) {
        this.second = second;
    }
}
