package com.mojia.stock.domain;

import com.mojia.stock.TimePeriodType;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 一条K线,可能属于不同周期
 * Created by wangxin on 16/7/5.
 */
public class KBarDo {
    private String symbol;
    private double close;
    private double adjClose;
    private double open;
    private double high;
    private double low;
    private TimePeriodType timePeriodType;
    private long volume;

    //顶分型的顶
    private boolean isPeakPoint;
    //底分型
    private boolean isBottomPoint;

    private Date date;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(double adjClose) {
        this.adjClose = adjClose;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isPeakPoint() {
        return isPeakPoint;
    }

    public void setPeakPoint(boolean peakPoint) {
        isPeakPoint = peakPoint;
    }

    public boolean isBottomPoint() {
        return isBottomPoint;
    }

    public void setBottomPoint(boolean bottomPoint) {
        isBottomPoint = bottomPoint;
    }

    public TimePeriodType getTimePeriodType() {
        return timePeriodType;
    }

    public void setTimePeriodType(TimePeriodType timePeriodType) {
        this.timePeriodType = timePeriodType;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KBarDo kBarDo = (KBarDo) o;

        return date != null ? date.equals(kBarDo.date) : kBarDo.date == null;

    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(new SimpleDateFormat("yyyyMMdd").format(date));
        sb.append(" ");
        sb.append("high=" + high + " ");
        if (isPeakPoint) {
            sb.append("顶分型 ");
        }
        if (isBottomPoint) {
            sb.append("底分型 ");
        }

        return sb.toString();
    }
}
