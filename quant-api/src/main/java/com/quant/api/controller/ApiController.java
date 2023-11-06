package com.quant.api.controller;

import com.quant.api.aspect.option.AllowAccessIp;
import com.quant.core.enums.AmtRange;
import com.quant.core.enums.QuarterCode;
import com.quant.core.utils.DateUtils;
import com.quant.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@AllowAccessIp
@RestController
@RequiredArgsConstructor
public class ApiController {
    private final StockService stockService;


    @PostMapping(value = "user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setUser(@RequestParam(value = "email") String email) {
        stockService.setUserInfo(email);

        return ResponseEntity.ok("User Set");
    }


    @PostMapping(value = "price", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setStockPrice(@RequestParam(value = "sDate", required = false, defaultValue = "") String sDate,
                                        @RequestParam(value = "eDate", required = false, defaultValue = "") String eDate) {

        if (StringUtils.hasText(sDate) && StringUtils.hasText(eDate)) {
            LocalDate startDate = DateUtils.toStringLocalDate(sDate);
            LocalDate endDate = DateUtils.toStringLocalDate(eDate);

            //기준일자 까지 반복
            while (!endDate.equals(startDate)) {
                stockService.getKrxDailyInfo(startDate);
                startDate = startDate.plusDays(1);
            }
        } else if (StringUtils.hasText(sDate) && !StringUtils.hasText(eDate)) {
            stockService.getKrxDailyInfo(DateUtils.toStringLocalDate(sDate));
        } else {
            stockService.getKrxDailyInfo(LocalDate.now());
        }

        return ResponseEntity.ok("SET PRICE");
    }


    @PostMapping(value = "code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setCorpCode() {
        stockService.getDartCorpCodeInfo();
        return ResponseEntity.ok("SET CODE");
    }


    @PostMapping(value = "fin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getFinance(@RequestParam(value = "corpCode", required = false, defaultValue = "") String corpCode,
                                     @RequestParam(value = "quarter", required = false) QuarterCode quarter,
                                     @RequestParam(value = "year", required = false, defaultValue = "") String year) {
        if (!StringUtils.hasText(year)) {
            year = Integer.toString(LocalDate.now().getYear());
        }

        //특정 기업을 지정했을때
        if (StringUtils.hasText(corpCode)) {
            if (quarter != null) {
                stockService.setSingleCorpFinanceInfo(corpCode, year, quarter);
            } else {
                stockService.setSingleCorpFinanceInfo(corpCode, year);
            }
        } else {
            stockService.setMultiCorpFinanceInfo(year);
        }
        return ResponseEntity.ok("SET FINANCE");
    }


    @PostMapping(value = "average", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAveragePrice(@RequestParam(value = "sDate", required = false, defaultValue = "") String sDate,
                                          @RequestParam(value = "eDate", required = false, defaultValue = "") String eDate) {
        if (StringUtils.hasText(sDate) && StringUtils.hasText(eDate)) {
            LocalDate startDate = DateUtils.toStringLocalDate(sDate);
            LocalDate endDate = DateUtils.toStringLocalDate(eDate);

            while (!endDate.equals(endDate)) {
                stockService.setStockPriceAverage(startDate);
                startDate.plusDays(1);
            }
        } else if (StringUtils.hasText(sDate) && !StringUtils.hasText(eDate)) {
            stockService.setStockPriceAverage(DateUtils.toStringLocalDate(sDate));
        } else {
            stockService.setStockPriceAverage(LocalDate.now());
        }
        return ResponseEntity.ok("SET AVERAGE");
    }


    //포트폴리오 기반 추천 - 장기 추적용 데이터
    @GetMapping(value = "recommend", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRecommendStock(@RequestParam(value = "date", required = false, defaultValue = "") String date,
                                            @RequestParam(value = "portfolio", required = false, defaultValue = "") String portKey,
                                            @RequestParam(value = "save", required = false, defaultValue = "") Character saveYn) {

        LocalDate targetDate = DateUtils.toStringLocalDate(date);

        return ResponseEntity.ok(stockService.getStockRecommend(targetDate, portKey));
    }


    //일회성 추천
    @GetMapping(value = "recommend/ones", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getOneRecommendStock(@RequestParam(value = "date", required = false, defaultValue = "") String date,
                                               @RequestParam(value = "market", required = false, defaultValue = "ALL") String market,
                                               @RequestParam(value = "value", required = false, defaultValue = "10000000") Integer value,
                                               @RequestParam(value = "count", required = false, defaultValue = "20") Integer count,
                                               @RequestParam(value = "momentum", required = false, defaultValue = "0") Integer momentum,
                                               @RequestParam(value = "range", required = false, defaultValue = "ALL") AmtRange range,
                                               @RequestParam(value = "ratio", required = false, defaultValue = "Y") Character ratioYn,
                                               @RequestParam(value = "indicator", required = false, defaultValue = "") List<String> indicator
    ) {
        LocalDate targetDate = DateUtils.toStringLocalDate(date);
        return ResponseEntity.ok(stockService.getStockRecommendOne(targetDate, market, value, count, range, ratioYn, indicator, momentum));
    }

    // 포트폴리오 세팅
    @PostMapping(value = "port", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setPort(@RequestParam(value = "key") String userKey,
                                  @RequestParam(value = "momentum", required = false, defaultValue = "0") Integer momentum,
                                  @RequestParam(value = "value", defaultValue = "10000000") Integer value,
                                  @RequestParam(value = "count", defaultValue = "20") Integer count,
                                  @RequestParam(value = "loss", defaultValue = "50") Integer lossCut,
                                  @RequestParam(value = "range", defaultValue = "ALL") AmtRange range,
                                  @RequestParam(value = "market", defaultValue = "ALL") String market,
                                  @RequestParam(value = "indicator", defaultValue = "") List<String> indicator,
                                  @RequestParam(value = "rebalance", defaultValue = "") List<String> rebalance,
                                  @RequestParam(value = "ratio", defaultValue = "Y") Character ratio,
                                  @RequestParam(value = "comment", defaultValue = "") String comment) {

        stockService.setPortfolio(userKey, momentum, value, count, lossCut, range, market, indicator, rebalance, ratio, comment );

        return ResponseEntity.ok("Set Portfolio ");
    }

}
