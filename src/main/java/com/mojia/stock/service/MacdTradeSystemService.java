package com.mojia.stock.service;

import java.util.Date;
import java.util.List;

import com.mojia.stock.common.TimePeriodType;
import com.mojia.stock.domain.KBarDo;
import com.mojia.stock.domain.MacdDo;

/**
 * MACD交易系统测试
 * 
 * @author xinwang
 *
 */
public class MacdTradeSystemService {
    private QuoteService quoteService;
    private MAService maService;
    private MACDService macdService;
    private int maParam = 120;

    public void trade(String symbol) {
        List<KBarDo> kBars = quoteService.loadQuote(symbol);
        List<MacdDo> macdDos = macdService.macd(symbol, TimePeriodType.dayPeriodType);
        
        for (KBarDo each : kBars) {
              if(isBuy(symbol, each, kBars)) {
                  
              }

        }

    }


    private boolean isBuy(String symbol, KBarDo current, List<KBarDo> kBars) {
        MacdDo macdDo = macdService.macd(symbol, TimePeriodType.dayPeriodType, current.getDate());
        double ma = maService.ma(symbol, current.getDate(), TimePeriodType.dayPeriodType, maParam);

        if (current.getLow() > ma && cross(current,kBars)) {

        }
        return false;
    }


    /**
     * MACD交叉
     * @param current
     * @param kBars
     * @return
     */
    private boolean cross(KBarDo current, List<MacdDo> macdDos) {

        return false;
    }
}
