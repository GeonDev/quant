package com.quant.stock.service;


import com.quant.stock.entity.StockAverage;
import com.quant.stock.entity.StockPrice;
import com.quant.stock.model.enums.PriceType;
import com.quant.stock.repository.StockAverageRepository;
import com.quant.stock.repository.StockPriceRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AsyncService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final StockPriceRepository stockPriceRepository;

    private final StockAverageRepository stockAverageRepository;


    @Async
    public void asyncStockPriceAverage (String stockCode, LocalDate targetDate, PriceType priceType){

        PageRequest pageRequest = PageRequest.of(0, priceType.getValue(), Sort.by("BAS_DT").descending());
        List<StockPrice> priceList = stockPriceRepository.findByStockCodeAndBasDtBefore(stockCode, targetDate, pageRequest);


        StockAverage average = new StockAverage();
        average.setStockCode(stockCode);
        average.setTarDt(targetDate);
        average.setPriceType(priceType);

        Integer totalPrice = 0;
        if(priceList.size() == priceType.getValue()){
            for(StockPrice price : priceList ){
                totalPrice += price.getEndPrice();
            }
            average.setPrice(totalPrice/priceType.getValue());
        }else{
            //기간 개수가 모자르다면 평균을 0으로 처리
            average.setPrice(0);
        }

        stockAverageRepository.save(average);
    }

}
