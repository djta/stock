package com.mojia.stock.service;

import com.mojia.stock.common.TimePeriodType;
import com.mojia.stock.domain.KBarDo;

import java.util.Date;
import java.util.List;

import static com.mojia.stock.common.TimePeriodType.dayPeriodType;

/**
 * Created by xinwang on 7/21/16.
 */
public class MAService {
    private QuoteService quoteService;

    public double ma(String symbol, Date date, TimePeriodType type, int maParam) {
        long time = beforeTime(type, maParam);

        Date afterDate = new Date(date.getTime() - time);

        List<KBarDo> lastKbars = quoteService.loadQuote(symbol, afterDate);

        double ma = 0;
        for (KBarDo each : lastKbars) {
            ma += each.getClose();
        }

        return ma * 1.0 / maParam;
    }

    private long beforeTime(TimePeriodType type, int maParam) {
        switch (type) {
            case dayPeriodType:
                return 24 * 60 * 60 * 1000 * maParam;
            case minu30PeriodType:

            default:
                return 0;

        }
    }

    public void setQuoteService(QuoteService quoteService) {
        this.quoteService = quoteService;
    }
}
