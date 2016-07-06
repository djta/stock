package com.mojia.stock.domain;

import com.mojia.stock.TimePeriodType;

import java.util.Date;

/**一条K线,可能属于不同周期
 * Created by wangxin on 16/7/5.
 */
public class KBarDo {
    private double close;
    private double open;
    private double high;
    private double low;
    private TimePeriodType timePeriodType;

    //顶分型的顶
    private boolean isPeakPoint;
    //底分型
    private boolean isBottomPoint;

    private Date date;

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
}
