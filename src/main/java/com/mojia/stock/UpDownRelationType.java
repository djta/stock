package com.mojia.stock;

/**
 * 两根K线关系
 * Created by wangxin on 16/7/6.
 *    |
 *  | |  UP       |
 *  |           | |     UPContains
 *              | |
 *                |
 *
 *  |
 *  | |  Down     |
 *    |         | |     DownContains
 *              | |
 *              |
 */
public enum UpDownRelationType {
    UP(0),
    Down(1),
    UPContains(2),
    DownContains(3);

    private int code;

    private UpDownRelationType(int code) {
        this.code = code;
    }

}
