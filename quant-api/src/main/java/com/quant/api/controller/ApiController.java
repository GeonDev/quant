package com.quant.api.controller;

import com.quant.api.aspect.option.AllowAccessIp;
import com.quant.core.utils.DateUtils;
import com.quant.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


@AllowAccessIp
@RestController
@RequiredArgsConstructor
public class ApiController {

    private final StockService stockService;

    @GetMapping(value = "daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStockPrice(@RequestParam(value = "date", required = false, defaultValue = "") String date){
        LocalDate targetDate = DateUtils.toStringLocalDate(date);

        stockService.getKrxDailyInfo(targetDate);
        return ResponseEntity.ok("");
    }

    @GetMapping(value = "code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCorpCode( ){
        stockService.getDartCorpCodeInfo();
        return ResponseEntity.ok("");
    }

    @GetMapping(value = "fin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getFinance( @RequestParam(value = "corpCode") String corpCode,
                                      @RequestParam(value = "reprtCode") String reprtCode,
            @RequestParam(value = "year", required = false, defaultValue = "") String year  ){

          if(year.equals("")){
              year = Integer.toString(LocalDate.now().getYear());
          }

        stockService.setCorpFinanceInfo( corpCode, year , reprtCode );
        return ResponseEntity.ok("");
    }

    @GetMapping(value = "average", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAveragePrice(@RequestParam(value = "date", required = false, defaultValue = "") String date){
        LocalDate targetDate = DateUtils.toStringLocalDate(date);

        stockService.setStockPriceAverage(targetDate);
        return ResponseEntity.ok("");
    }

}
