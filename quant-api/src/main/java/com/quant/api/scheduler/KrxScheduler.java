package com.quant.api.scheduler;

import com.quant.stock.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class KrxScheduler {

    private final ApiService apiService;

    @Scheduled(cron = "${cron.krx.daily-info}")
    private void koreaStockInfoScheduler(){
        apiService.getKrxDailyInfo();
        apiService.getDartCorpCodeInfo();
        apiService.setStockPriceAverage();
    }

    @Scheduled(cron = "${cron.krx.month-info}")
    private void koreaStockFinanceScheduler(){
        apiService.getCorpFinanceInfo();
    }

}
