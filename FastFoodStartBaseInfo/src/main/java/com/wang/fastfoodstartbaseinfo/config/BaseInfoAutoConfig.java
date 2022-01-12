package com.wang.fastfoodstartbaseinfo.config;

import feign.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Auther: wAnG
 * @Date: 2022/1/12 15:51
 * @Description:
 */

@Configuration
@Import({FeignConfig.class,SpringContext.class,WebInfoConfig.class})
public class BaseInfoAutoConfig {

    @Bean
    @ConditionalOnClass(Logger.class)
    public FeignConfig feignConfig(){
        return new FeignConfig();
    }

    @ConditionalOnClass(Logger.class)
    @Bean
    public RemoteLogger remoteLogger(){
        return new RemoteLogger();
    }

}
