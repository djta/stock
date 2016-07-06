package com.mojia.stock.service;

import com.mojia.stock.TimePeriodType;
import com.mojia.stock.domain.KBarDo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by wangxin on 16/7/5.
 */
@Service
public class KBarService {

    public List<KBarDo> getKBarData(long startSecond, long endSecond, TimePeriodType timePeriodType) {


        return Collections.emptyList();
    }

}
