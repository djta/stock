package com.mojia.stock;

import com.mojia.stock.domain.BiLineDo;
import com.mojia.stock.service.BiLineService;
import com.mojia.stock.service.QuoteService;

import java.util.List;

/**
 * Created by wangxin on 16/7/5.
 */
public class Lancher {

    public static void main(String[] args) {
        List<BiLineDo> biLineDos = new BiLineService().parseBiLine(new QuoteService().loadQuote("000002.sz"));



        System.out.println(biLineDos.size());
    }
}
