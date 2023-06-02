package com.quant.stock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {

    @Value("${signkey.data-go}")
    String poolSize;

    String maxPoolSize;


    String capacitySize;

    String threadName;


    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(Integer.parseInt(capacitySize));
        executor.setThreadNamePrefix(threadName);
        executor.initialize();
        return executor;
    }
}
