package com.mojia.stock.service;

import com.mojia.stock.common.TimePeriodType;
import com.mojia.stock.domain.KBarDo;
import com.mojia.stock.domain.MacdDo;

import java.util.Date;
import java.util.List;

import static com.mojia.stock.common.TimePeriodType.*;

/**
 * Created by xinwang on 7/21/16.
 */
public class MACDService {
    private QuoteService quoteService;
    private static final int shortValue = 12;
    private static final int longValue = 26;
    private static final int midValue = 9;
    private static final double aFast = 2.0 / 13;
    private static final double aSlow = 2.0 / 27;


    public MacdDo macd(String symbol, TimePeriodType type, Date date) {
        Date fastDate = new Date(beforeTime(type, 12));
        Date slowDate = new Date(beforeTime(type,26));
        Date midDate = new Date(beforeTime(type,9));

        List<KBarDo> fastkBarDos = quoteService.loadQuote(symbol, fastDate);
        List<KBarDo> slowKBarDos = quoteService.loadQuote(symbol,slowDate);
        List<KBarDo> midKBarDos = quoteService.loadQuote(symbol,midDate);


        double dif = ema(fastkBarDos,aFast) - ema(slowKBarDos, aSlow);
        double dea = ema(midKBarDos,)

    }

    private long beforeTime(TimePeriodType type, int count) {
        switch (type) {
            case dayPeriodType:
                return count * 24 * 60 * 60 * 1000;
        }

        return 0;
    }

    private double ema(List<KBarDo> kBarDos, double a) {
        double ema = 0;

        double sumA = 0;
        double sumB = 0;
        for (int i = 0; i < kBarDos.size(); i++) {
            KBarDo bar = kBarDos.get(0);

            sumA += bar.getClose() * Math.pow((1 - a), i);
            sumB += Math.pow((1 - a), i);
        }

        ema = sumA * 1.0 / sumB;

        return ema;
    }


    public QuoteService getQuoteService() {
        return quoteService;
    }

    public void setQuoteService(QuoteService quoteService) {
        this.quoteService = quoteService;
    }
}
