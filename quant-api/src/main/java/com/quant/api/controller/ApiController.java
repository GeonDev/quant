package com.quant.api.controller;

import com.quant.core.utils.DateUtils;
import com.quant.stock.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;

    @GetMapping(value = "daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStockPrice(@RequestParam(value = "date", required = false, defaultValue = "") String date){
        LocalDate targetDate = DateUtils.toStringLocalDate(date);

        apiService.getKrxDailyInfo(targetDate);
        return ResponseEntity.ok("");
    }

    @GetMapping(value = "code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCorpCode( ){
        apiService.getDartCorpCodeInfo();
        return ResponseEntity.ok("");
    }

    @GetMapping(value = "fin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getFinance( ){
        apiService.getCorpFinanceInfo( "00565154", "2021" , "11013" );
        return ResponseEntity.ok("");
    }

    @GetMapping(value = "average", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAveragePrice(@RequestParam(value = "date", required = false, defaultValue = "") String date){
        LocalDate targetDate = DateUtils.toStringLocalDate(date);

        apiService.setStockPriceAverage(targetDate);
        return ResponseEntity.ok("");
    }

}
