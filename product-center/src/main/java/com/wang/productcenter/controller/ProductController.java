package com.wang.productcenter.controller;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.Util.SqlResultUtil;
import com.wang.fastfood.apicommons.entity.DTO.ProductDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.productcenter.entity.BO.Product;
import com.wang.productcenter.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 21:59
 * @Description:
 */

@RestController
@CrossOrigin
@RequestMapping("/product")
@SuppressWarnings("all")
public class ProductController {

    @Autowired
    private IProductService productService;

    @PostMapping("/getAll")
    public Response<PageInfo<ProductDTO>> getAll(@RequestBody ProductDTO productDTO){
        Product product = buildBO(productDTO);
        PageInfo<Product> result = productService.getAll(product);
        return ResponseUtil.success(PageUtils.getPageInfo(result,result.getList()
                .stream()
                .map(Product::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/getById")
    public Response<ProductDTO> getById(ProductDTO productDTO){
        Product product = buildBO(productDTO);
        Product result = productService.getById(product);
        return ResponseUtil.success(result != null ? result.doBackward() : null);
    }

    @PostMapping("/getByName")
    public Response<ProductDTO> getByName(ProductDTO productDTO){
        Product product = buildBO(productDTO);
        Product result = productService.getByName(product);
        return ResponseUtil.success(result != null ? result.doBackward() : null);
    }

    @PostMapping("/likeByName")
    public Response<PageInfo<ProductDTO>> likeByName(ProductDTO productDTO){
        Product product = buildBO(productDTO);
        PageInfo<Product> result = productService.likeByName(product);
        return ResponseUtil.success(PageUtils.getPageInfo(result,result.getList()
                .stream()
                .map(Product::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/delete")
    public Response delete(ProductDTO productDTO){
        Product product = buildBO(productDTO);
        productService.remove(product);
        return ResponseUtil.success();
    }

    @PostMapping("/update")
    public Response update(ProductDTO productDTO){
        Product product = buildBO(productDTO);
        int result = productService.update(product);
        return ResponseUtil.success(SqlResultUtil.updateResult(result));
    }

    private Product buildBO(ProductDTO productDTO){
        return BOUtils.convert(Product.class, productDTO);
    }

}
