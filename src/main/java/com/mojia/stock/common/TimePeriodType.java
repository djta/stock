package com.mojia.stock.common;

/**
 * Created by wangxin on 16/7/5.
 */
public enum TimePeriodType {
    weekPeriodType(0),
    dayPeriodType(1),
    minu30PeriodType(2),
    minu5FPeriodType(3),
    minu1FPeriodType(4);

    private int code;

    private TimePeriodType(int code) {
        this.code = code;
    }
}
