package com.wang.fastfoodstartbaseinfo.config;

import com.wang.fastfoodstartbaseinfo.interceptor.LogAnalysis;
import com.wang.fastfoodstartbaseinfo.interceptor.RequestInterceptor;
import com.wang.fastfoodstartbaseinfo.interceptor.SqlCostInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Auther: wAnG
 * @Date: 2022/1/12 16:05
 * @Description:
 */

@Configuration
public class WebInfoConfig {

    @Bean
    @ConditionalOnClass(ResponseBodyAdvice.class)
    public LogAnalysis logAnalysis(){
        return new LogAnalysis();
    }

    @Bean
    @ConditionalOnClass(HandlerInterceptor.class)
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor();
    }

    @Bean
    @ConditionalOnClass(Interceptor.class)
    public SqlCostInterceptor sqlCostInterceptor(){
        return new SqlCostInterceptor();
    }

}
