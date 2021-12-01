package com.wang.productcenter.controller;

import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.ProductTypeDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.productcenter.entity.BO.ProductType;
import com.wang.productcenter.service.impl.ProductTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/11/26 01:32
 * @Description:
 */

@RestController
@RequestMapping("type")
@SuppressWarnings("all")
public class ProductTypeController {

    @Autowired
    ProductTypeServiceImpl productTypeService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/getAll")
    public Response<List<ProductTypeDTO>> getAll(){
        List<ProductType> typePOList = productTypeService.getAll();
        return ResponseUtil.success(typePOList.stream().map(ProductType::doBackward).collect(Collectors.toList()));
    }

    @PostMapping("/getById")
    public Response<ProductTypeDTO> getTypeById(ProductType productTypeDTO){
        ProductType productType = BOUtils.convert(ProductType.class, productTypeDTO);
        ProductType type = productTypeService.getById(productType);
        return ResponseUtil.success(type.doBackward());
    }

    @PostMapping("/getByName")
    public Response<List<ProductTypeDTO>> getTypeByName(ProductType productTypeDTO){
        ProductType productType = BOUtils.convert(ProductType.class, productTypeDTO);
        List<ProductType> typeList = productTypeService.getByName(productType);
        return ResponseUtil.success(typeList.stream().map(ProductType::doBackward).collect(Collectors.toList()));
    }

    @PostMapping("/delete")
    public Response remove(ProductType productTypeDTO){
        ProductType productType = BOUtils.convert(ProductType.class, productTypeDTO);
        productTypeService.removeType(productType);
        return ResponseUtil.success();
    }

    @PostMapping("/update")
    public Response update(ProductTypeDTO productTypeDTO){
        ProductType productType = BOUtils.convert(ProductType.class, productTypeDTO);
        productTypeService.updateType(productType);
        return ResponseUtil.success();
    }



}
