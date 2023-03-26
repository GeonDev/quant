package com.stock.quant.api.controller;

import com.stock.quant.api.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;

    @GetMapping(value = "daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStockPrice( ){
        apiService.getKrxDailyInfo();
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

}
