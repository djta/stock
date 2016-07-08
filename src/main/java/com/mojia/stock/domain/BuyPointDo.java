package com.mojia.stock.domain;

/**
 * Created by wangxin on 16/7/8.
 */
public class BuyPointDo {
    private KBarDo kBarDo;
    private double price;
    //多少股
    private int count;

    public KBarDo getkBarDo() {
        return kBarDo;
    }

    public void setkBarDo(KBarDo kBarDo) {
        this.kBarDo = kBarDo;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
