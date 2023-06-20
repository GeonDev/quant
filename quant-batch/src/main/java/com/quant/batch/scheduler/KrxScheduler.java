package com.quant.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class KrxScheduler {


    @Scheduled(cron = "${cron.krx.daily-info}")
    private void koreaStockInfoScheduler(){


    }

    @Scheduled(cron = "${cron.krx.month-info}")
    private void koreaStockFinanceScheduler(){


    }

}
