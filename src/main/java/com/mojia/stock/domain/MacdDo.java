package com.mojia.stock.domain;

import java.sql.Date;

/**
 * Created by xinwang on 7/21/16.
 */
public class MacdDo {
    private double dif;
    private double dea;
    private double macd;
    private Date date;

    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getMacd() {
        return macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }

    public double getDif() {
        return dif;
    }

    public void setDif(double dif) {
        this.dif = dif;
    }

    public double getDea() {
        return dea;
    }

    public void setDea(double dea) {
        this.dea = dea;
    }
}
