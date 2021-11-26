package com.wang.fastfoodapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class fastFoodApiApplicationTests {

    @Test
    void contextLoads() {
        String s = "http://192.168.31.69:8091/test";
        String[] split = s.split("/");
        System.out.println(split.length);
        System.out.println(split[2]);
    }

}
