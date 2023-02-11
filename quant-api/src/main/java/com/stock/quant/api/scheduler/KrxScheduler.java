package com.stock.quant.api.scheduler;

import com.stock.quant.api.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KrxScheduler {

    private final ApiService apiService;

    @Scheduled(cron = "${cron.krx.daily-info}")
    private void koreaStockInfoScheduler(){
        apiService.getKrxDailyInfo();
    }

}
