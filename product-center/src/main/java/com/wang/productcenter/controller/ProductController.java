package com.wang.productcenter.controller;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.ProductDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.productcenter.entity.BO.Product;
import com.wang.productcenter.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 21:59
 * @Description:
 */

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping("/getAll")
    public Response<List<ProductDTO>> getAll(){
        List<Product> result = productService.getAll();
        return ResponseUtil.success(result.stream().map(Product::doBackward).collect(Collectors.toList()));
    }

}
