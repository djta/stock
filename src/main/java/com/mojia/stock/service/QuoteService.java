package com.mojia.stock.service;

import com.mojia.stock.common.TimePeriodType;
import com.mojia.stock.domain.KBarDo;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by wangxin on 16/7/6.
 * //http://table.finance.yahoo.com/table.csv?s=000001.sz
 */
@Service
public class QuoteService {
    private static final String dir = "/Users/wangxin/Downloads/stock-data/";

    public static void main(String[] args) {
        new QuoteService().test();
    }

    private void test() {
        loadQuote("000001.sz");
    }

    private boolean notTheSame(KBarDo lastKBarDo, KBarDo kBarDo) {
        if (lastKBarDo != null && lastKBarDo.getLow() == kBarDo.getLow() &&
                lastKBarDo.getHigh() == kBarDo.getHigh() &&
                lastKBarDo.getOpen() == kBarDo.getOpen() &&
                lastKBarDo.getAdjClose() == kBarDo.getAdjClose()) {
            return false;
        }

        return true;
    }

    private KBarDo parseKLine(String line) {
        String[] array = line.split(",");
        if (array.length == 7) {
            KBarDo bar = new KBarDo();

            try {
                bar.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(array[0]));
                bar.setTimePeriodType(TimePeriodType.dayPeriodType);
                bar.setOpen(Double.parseDouble(array[1]));
                bar.setHigh(Double.parseDouble(array[2]));
                bar.setLow(Double.parseDouble(array[3]));
                bar.setClose(Double.parseDouble(array[4]));
                bar.setVolume(Long.parseLong(array[5]));
                bar.setAdjClose(Double.parseDouble(array[6]));

                return bar;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public List<KBarDo> loadQuote(String symbol, Date afterDate) {
        List<KBarDo> kBarDos = new ArrayList<KBarDo>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(dir + symbol + ".csv")));
            String line = "";

            KBarDo lastKBarDo = null;
            while ((line = reader.readLine()) != null) {
                KBarDo kBarDo = parseKLine(line);

                if (kBarDo != null &&
                        (kBarDo.getDate().compareTo(afterDate) > 0) &&
                        notTheSame(lastKBarDo, kBarDo)) {
                    kBarDos.add(kBarDo);
                    lastKBarDo = kBarDo;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.reverse(kBarDos);

        return kBarDos;
    }

    public List<KBarDo> loadQuote(String symbol) {
        Date afterDate = new Date();
        afterDate.setTime(0);

        return loadQuote(symbol, afterDate);
    }

}
