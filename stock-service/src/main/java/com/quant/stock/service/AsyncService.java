package com.quant.stock.service;


import com.quant.stock.model.enums.PriceType;
import com.quant.stock.repository.StockPriceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class AsyncService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final StockPriceRepository stockPriceRepository;


    @Async
    public void asyncStockPriceAverage (String stockCode, LocalDate targetDate, PriceType priceType){

        switch (priceType ){

            case DAY5 :
                break;

            case DAY20 :
                break;

            case DAY60 :
                break;

            case DAY120 :
                break;

            case DAY200 :
                break;

            case DAY240 :
                break;

        }










    }
}
