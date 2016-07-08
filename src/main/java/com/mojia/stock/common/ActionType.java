package com.mojia.stock.common;

/**
 * Created by wangxin on 16/7/8.
 */
public enum ActionType {
    CreatePosition(0),//建仓
    IncreasePosition(1),//加仓
    DecreasePosition(2),//减仓
    CleanPosition(3);//清仓

    private int code;

    private ActionType(int code) {
        this.code = code;
    }
}
