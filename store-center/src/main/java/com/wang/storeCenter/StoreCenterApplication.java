package com.wang.storeCenter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class StoreCenterApplication {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SpringApplication.run(StoreCenterApplication.class, args);
        long endTime = System.currentTimeMillis();
        long TotalTime = endTime - startTime;
        log.info("[Start-Success (TotalTime : {}ms)] ==> ServerName : [Product-Center]",TotalTime);
    }

}
