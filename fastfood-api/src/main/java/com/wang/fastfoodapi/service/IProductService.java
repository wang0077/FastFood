package com.wang.fastfoodapi.service;

import com.wang.fastfoodapi.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Auther: wAnG
 * @Date: 2021/11/26 19:21
 * @Description:
 */
@FeignClient(value = "Product-Center",configuration = FeignConfig.class)
public interface IProductService {

    @RequestMapping(method = RequestMethod.POST,value = "test")
    public String test();
}

