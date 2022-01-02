package com.wang.productcenter;

import com.wang.productcenter.Util.RedisUtil;
import com.wang.productcenter.config.SpringContext;
import com.wang.productcenter.Interceptor.SqlCostInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
//@EnableDiscoveryClient
@EnableFeignClients
@Slf4j
public class ProductCenterApplication {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SpringApplication.run(ProductCenterApplication.class, args);
        init();
        long endTime = System.currentTimeMillis();
        long TotalTime = endTime - startTime;
        log.info("[Start-Success (TotalTime : {}ms)] ==> ServerName : [Product-Center]",TotalTime);
    }

    private static void init(){
        SpringContext.getBean(SqlCostInterceptor.class);
    }

}
