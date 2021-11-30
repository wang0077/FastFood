package com.wang.productcenter;

import com.wang.productcenter.Util.RedisUtil;
import com.wang.productcenter.config.SpringContext;
import com.wang.productcenter.Interceptor.SqlCostInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
//@EnableDiscoveryClient
@EnableFeignClients
public class ProductCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCenterApplication.class, args);
        init();
    }

    private static void init(){
        SpringContext.getBean(SqlCostInterceptor.class);
    }

}
