package com.quant.api.controller;

import com.quant.api.aspect.option.AllowAccessIp;
import com.quant.core.entity.UserInfo;
import com.quant.core.repository.UserInfoRepository;
import com.quant.core.utils.DateUtils;
import com.quant.stock.service.StockService;
import lombok.RequiredArgsConstructor;
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

    private final UserInfoRepository userInfoRepository;


    @GetMapping(value = "code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCorpCode() {
        stockService.getDartCorpCodeInfo();
        return ResponseEntity.ok("SET CODE");
    }


    @GetMapping(value = "price", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStockPrice(@RequestParam(value = "date", required = false, defaultValue = "") String date) {
        LocalDate targetDate = LocalDate.now();

        if (StringUtils.hasText(date)) {
            targetDate = DateUtils.toLocalDate(date);
        }

        stockService.getKrxDailyInfo(targetDate);
        return ResponseEntity.ok("SET PRICE");
    }

    @GetMapping(value = "fin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getFinance(@RequestParam(value = "corpCode", required = false, defaultValue = "") String corpCode,
                                     @RequestParam(value = "reprtCode", required = false, defaultValue = "") String reprtCode,
                                     @RequestParam(value = "year", required = false, defaultValue = "") String year) {

        if (!StringUtils.hasText(year)) {
            year = Integer.toString(LocalDate.now().getYear());
        }

        if(StringUtils.hasText(corpCode) && StringUtils.hasText(reprtCode) ){
            stockService.setCorpFinanceInfo(corpCode, year, reprtCode);
        }else {
            //회사 목록 전체의 재무재표 업데이트
            stockService.setCorpFinanceInfo();
        }


        return ResponseEntity.ok("SET FINANCE");
    }

    @GetMapping(value = "average", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAveragePrice(@RequestParam(value = "date", required = false, defaultValue = "") String date) {
        LocalDate targetDate = LocalDate.now();

        if (StringUtils.hasText(date)) {
            targetDate = DateUtils.toStringLocalDate(date);
        }

        stockService.setStockPriceAverage(targetDate);
        return ResponseEntity.ok("SET AVERAGE");
    }

    @GetMapping(value = "recommend", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRecommendStock(@RequestParam(value = "date", required = false, defaultValue = "") String date) {

        return ResponseEntity.ok("");
    }

    @GetMapping(value = "user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setUser() {
        UserInfo d = new UserInfo();
        userInfoRepository.save(d);

        return ResponseEntity.ok("");
    }

}
