package com.wang.tradecenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TradeCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeCenterApplication.class, args);
    }

}
