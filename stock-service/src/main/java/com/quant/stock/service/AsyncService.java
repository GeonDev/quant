package com.quant.stock.service;

import com.quant.stock.entity.CorpCode;
import com.quant.stock.model.enums.PriceType;
import com.quant.stock.repository.CorpCodeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsyncService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CorpCodeRepository corpCodeRepository;

    @Async
    public void asyncStockPriceAverage (String stockCode, PriceType priceType){

    }
}
