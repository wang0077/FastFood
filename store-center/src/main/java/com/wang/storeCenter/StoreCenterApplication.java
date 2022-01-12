package com.wang.storeCenter;

import com.wang.storeCenter.Interceptor.SqlCostInterceptor;
import com.wang.storeCenter.config.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class StoreCenterApplication {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SpringApplication.run(StoreCenterApplication.class, args);
        init();
        long endTime = System.currentTimeMillis();
        long TotalTime = endTime - startTime;
        log.info("[Start-Success (TotalTime : {}ms)] ==> ServerName : [Product-Center]",TotalTime);
        RedissonClient bean = SpringContext.getBean(RedissonClient.class);
        System.out.println(bean);
    }

    private static void init(){
        SpringContext.getBean(SqlCostInterceptor.class);
    }
}
