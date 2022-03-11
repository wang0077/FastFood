package com.wang.productcenter;

import com.wang.fastfootstartredis.Util.RedisUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductCenterApplicationTests {

    @Autowired
    RocketMQTemplate rocketMQTemplate;


    @Test
    public void S (){
        RedisUtil.zadd("test",1,1);
    }

}
