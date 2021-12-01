package com.wang.productcenter.controller;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.productcenter.entity.BO.ProductType;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.productcenter.service.impl.ProductTypeServiceImpl;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/getById")
    public Response<ProductType> getTypeById(ProductType productType){
        ProductType type = productTypeService.getById(productType);
        return ResponseUtil.success(type);
    }

    @PostMapping("/getByName")
    public Response<List<ProductType>> getTypeByName(ProductType productType){
        List<ProductType> typeList = productTypeService.getByName(productType);
        return ResponseUtil.success(typeList);
    }

    @PostMapping("/delete")
    public Response remove(ProductType productType){
        productTypeService.removeType(productType);
        return ResponseUtil.success();
    }

    @PostMapping("/update")
    public Response update(ProductType productType){
        productTypeService.updateType(productType);
        return ResponseUtil.success();
    }



}
