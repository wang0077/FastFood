package com.wang.productcenter.controller;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.BO.ProductType;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.entity.PO.ProductTypePO;
import com.wang.productcenter.entity.response.ProductTypeResponse;
import com.wang.productcenter.service.impl.ProductTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/11/26 01:32
 * @Description:
 */

@RestController
@RequestMapping("type")
public class ProductTypeController {

    @Autowired
    ProductTypeServiceImpl productTypeService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/getAll")
    public Response<List<ProductType>> getAll(){
        List<ProductType> typePOList = productTypeService.getAll();
        return ResponseUtil.success(typePOList);
    }

    @GetMapping("/test")
    public String test(){
        return "????";
    }



}
