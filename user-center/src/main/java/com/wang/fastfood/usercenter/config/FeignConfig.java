package com.wang.fastfood.usercenter.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: wAnG
 * @Date: 2021/11/26 01:44
 * @Description: OpenFeign日志等级设置
 */

@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
