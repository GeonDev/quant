package com.quant.api.controller;

import com.quant.api.aspect.option.AllowAccessIp;
import com.quant.core.enums.QuarterCode;
import com.quant.core.utils.DateUtils;
import com.quant.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@AllowAccessIp
@RestController
@RequiredArgsConstructor
public class ApiController {
    private final StockService stockService;


    @GetMapping(value = "user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setUser(@RequestParam(value = "email") String email) {
        return ResponseEntity.ok("User key : " + stockService.setUserInfo(email));
    }


    @GetMapping(value = "code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCorpCode() {
        stockService.getDartCorpCodeInfo();
        return ResponseEntity.ok("SET CODE");
    }

    @GetMapping(value = "price", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStockPrice(@RequestParam(value = "sDate", required = false, defaultValue = "") String sDate,
                                        @RequestParam(value = "eDate", required = false, defaultValue = "") String eDate) {

        if (StringUtils.hasText(sDate) && StringUtils.hasText(eDate)){
            LocalDate startDate = DateUtils.toStringLocalDate(sDate);
            LocalDate endDate = DateUtils.toStringLocalDate(eDate);

            while (!endDate.equals(startDate)){
                stockService.getKrxDailyInfo(startDate);
                startDate = startDate.plusDays(1);
            }
        }else if(StringUtils.hasText(sDate) && !StringUtils.hasText(eDate)){
            stockService.getKrxDailyInfo(DateUtils.toStringLocalDate(sDate));
        }else {
            stockService.getKrxDailyInfo(LocalDate.now());
        }

        return ResponseEntity.ok("SET PRICE");
    }

    @GetMapping(value = "fin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getFinance(@RequestParam(value = "corpCode", required = false, defaultValue = "") String corpCode,
                                     @RequestParam(value = "quarter", required = false) QuarterCode quarter,
                                     @RequestParam(value = "year", required = false, defaultValue = "") String year) {
        if (!StringUtils.hasText(year)) {
            year = Integer.toString(LocalDate.now().getYear());
        }

        //특정 기업을 지정했을때
        if(StringUtils.hasText(corpCode)){
            if(quarter != null){
                stockService.setSingleCorpFinanceInfo(corpCode, year, quarter);
            }else {
                stockService.setSingleCorpFinanceInfo(corpCode, year);
            }
        }else{
            stockService.setMultiCorpFinanceInfo(year);
        }
        return ResponseEntity.ok("SET FINANCE");
    }

    @GetMapping(value = "average", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAveragePrice(@RequestParam(value = "sDate", required = false, defaultValue = "") String sDate,
                                          @RequestParam(value = "eDate", required = false, defaultValue = "") String eDate) {
        if (StringUtils.hasText(sDate) && StringUtils.hasText(eDate)){
            LocalDate startDate = DateUtils.toStringLocalDate(sDate);
            LocalDate endDate = DateUtils.toStringLocalDate(eDate);

            while (!endDate.equals(endDate)){
                stockService.setStockPriceAverage(startDate);
                startDate.plusDays(1);
            }
        }else if(StringUtils.hasText(sDate) && !StringUtils.hasText(eDate)){
            stockService.setStockPriceAverage(DateUtils.toStringLocalDate(sDate));
        }else {
            stockService.setStockPriceAverage(LocalDate.now());
        }
        return ResponseEntity.ok("SET AVERAGE");
    }

    @GetMapping(value = "recommend", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRecommendStock(@RequestParam(value = "date", required = false, defaultValue = "") String date) {

        return ResponseEntity.ok("");
    }


}
