package com.mojia.stock.domain;

import com.mojia.stock.common.ActionType;

/**
 * Created by wangxin on 16/7/8.
 */
public class ActionDo {
    private ActionType actionType;
    private int count;
    private String symbol;
    private KBarDo kBarDo;


    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public KBarDo getkBarDo() {
        return kBarDo;
    }

    public void setkBarDo(KBarDo kBarDo) {
        this.kBarDo = kBarDo;
    }
}
