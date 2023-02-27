package com.stock.quant.api.scheduler;

import com.stock.quant.api.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class KrxScheduler {

    private final ApiService apiService;

    //@Scheduled(cron = "${cron.krx.daily-info}")
    private void koreaStockInfoScheduler(){
        apiService.getKrxDailyInfo();
    }

}
