package com.wang.productcenter.service;

import com.oracle.tools.packager.Log;
import com.wang.productcenter.dao.ProductDao;
import com.wang.productcenter.entity.ProductPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @Auther: wAnG
 * @Date: 2021/11/27 01:44
 * @Description:
 */


@Service
@Slf4j
public class TestService {

    @Autowired
    ProductDao productDao;

    @Autowired
    RedissonClient client;


    public ProductPO findAll(){

        RMap<Object, Object> test = client.getMap("Test");
        HashMap<Object, Object> hashMap = new HashMap<>();
        test.put(1,1);
        test.put(2,2);
        test.putAll(hashMap);
        HashSet<Object> objects = new HashSet<>();
        test.getAll(objects);
        System.out.println(test.get(1));
        System.out.println(objects);
        System.out.println(objects.size());
        return productDao.findAll(1);
    }
}
