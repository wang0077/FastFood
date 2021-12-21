package com.wang.productcenter;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

@SpringBootTest
class ProductCenterApplicationTests {

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Autowired
    DiscoveryClient discoveryClient;

    @Test
    void contextLoads() {
        List<String> services = discoveryClient.getServices();

//        ProductPO productPO = new ProductPO();
//        productPO.setId(123);
//        productPO.setIsSales(1);
////        rocketMQTemplate.
//        rocketMQTemplate.convertAndSend("first-topic",productPO);
    }

//    public int S (Object a){
//        System.out.println(a.getClass());
//        if(a instanceof Number){
//            System.out.println("yes");
//        }
//    }

}
