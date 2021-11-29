package com.wang.productcenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wang.productcenter.Util.JSONUtil;
import com.wang.productcenter.Util.RedisUtil;
import com.wang.productcenter.entity.ProductPO;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

@SpringBootTest
class ProductCenterApplicationTests {


    @Test
    void contextLoads() {
        int a = 1;
        String s = JSONUtil.toJsonString(a);
        Object parse = JSONUtil.parse(s, Object.class);
        System.out.println(parse.getClass());

//        System.out.println(Object instanceof int);
//        System.out.println(qwe);
//        List parse = JSONUtil.parse(qwe, List.class);
//        System.out.println(parse);
//        JSON.parseObject("123",new TypeReference<List<ProductPO>>(){});
//        System.out.println(productPO1);
    }

//    public int S (Object a){
//        System.out.println(a.getClass());
//        if(a instanceof Number){
//            System.out.println("yes");
//        }
//    }

}
