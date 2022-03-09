package com.wang.productcenter;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductCenterApplicationTests {

    @Autowired
    RocketMQTemplate rocketMQTemplate;


//    public int S (Object a){
//        System.out.println(a.getClass());
//        if(a instanceof Number){
//            System.out.println("yes");
//        }
//    }

}
