package com.wang.productcenter.controller;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.Util.SqlResultUtil;
import com.wang.fastfood.apicommons.entity.DTO.ProductDetailDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.service.IProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 01:36
 * @Description:
 */

@RestController
@RequestMapping("/productDetail")
@CrossOrigin
@SuppressWarnings("all")
public class ProductDetailController {

    @Autowired
    private IProductDetailService productDetailService;

    @PostMapping("/getAll")
    public Response<PageInfo<ProductDetailDTO>> getAll(@RequestBody ProductDetailDTO productDetailDTO){
        ProductDetail productDetail = buildBO(productDetailDTO);
        List<ProductDetail> result = productDetailService.getAll(productDetail);
        return ResponseUtil.success(PageUtils.convertPage(result.stream()
                .map(ProductDetail::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/insert")
    public Response insert(ProductDetailDTO productDetailDTO){
        ProductDetail productDetail = buildBO(productDetailDTO);
        int result = productDetailService.insert(productDetail);
        return SqlResultUtil.insertResult(result);
    }

    @PostMapping("/getByName")
    public Response<ProductDetailDTO> getByName(ProductDetailDTO productDetailDTO){
        ProductDetail productDetail = buildBO(productDetailDTO);
        ProductDetail result = productDetailService.getByName(productDetail);
        return ResponseUtil.success(result != null ? result.doBackward() : null);
    }

    @PostMapping("/getById")
    public Response<ProductDetailDTO> getById(ProductDetailDTO productDetailDTO){
        ProductDetail productDetail = buildBO(productDetailDTO);
        ProductDetail result = productDetailService.getById(productDetail);
        return ResponseUtil.success(result != null ? result.doBackward() : null);
    }

    @PostMapping("/delete")
    public Response remove(ProductDetailDTO productDetailDTO){
        ProductDetail productDetail = buildBO(productDetailDTO);
        productDetailService.remove(productDetail);
        return ResponseUtil.success();
    }

    @PostMapping("getLikeName")
    public Response<PageInfo<ProductDetailDTO>> getLikeName(ProductDetailDTO productDetailDTO){
        ProductDetail productDetail = buildBO(productDetailDTO);
        List<ProductDetail> result = productDetailService.getLikeName(productDetail);
        return ResponseUtil.success(PageUtils.convertPage(result.stream()
                .map(ProductDetail::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/update")
    public Response update(ProductDetailDTO productDetailDTO){
        ProductDetail productDetail = buildBO(productDetailDTO);
        int result = productDetailService.update(productDetail);
        return SqlResultUtil.updateResult(result);
    }


    private ProductDetail buildBO(ProductDetailDTO productDetailDTO){
        return BOUtils.convert(ProductDetail.class, productDetailDTO);
    }

}
