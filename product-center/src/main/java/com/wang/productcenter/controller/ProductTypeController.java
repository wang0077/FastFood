package com.wang.productcenter.controller;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.Util.SqlResultUtil;
import com.wang.fastfood.apicommons.entity.DTO.ProductTypeDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.productcenter.entity.BO.ProductType;
import com.wang.productcenter.service.impl.ProductTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/11/26 01:32
 * @Description:
 */

@RestController
@CrossOrigin
@RequestMapping("type")
@SuppressWarnings("all")
public class ProductTypeController {

    @Autowired
    ProductTypeServiceImpl productTypeService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @PostMapping("/flush")
    public Response flushZset(){
        productTypeService.flush();
        return ResponseUtil.success();
    }

    @PostMapping("/getAll")
    public Response<PageInfo<ProductTypeDTO>> getAll(@RequestBody ProductTypeDTO productTypeDTO) {
        ProductType productType = buildBO(productTypeDTO);
        PageInfo<ProductType> result = productTypeService.getAll(productType);
        return ResponseUtil.success(PageUtils.getPageInfo(result, result.getList()
                .stream()
                .map(ProductType::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/insert")
    public Response insert(@RequestBody ProductTypeDTO productTypeDTO) {
        ProductType productType = buildBO(productTypeDTO);
        int result = productTypeService.insert(productType);
        return SqlResultUtil.insertResult(result);
    }

    @PostMapping("/getById")
    public Response<ProductTypeDTO> getTypeById(@RequestBody ProductTypeDTO productTypeDTO) {
        ProductType productType = buildBO(productTypeDTO);
        ProductType result = productTypeService.getById(productType);
        return ResponseUtil.success(result != null ? result.doBackward() : null);
    }

    @PostMapping("/getLikeName")
    public Response<PageInfo<ProductTypeDTO>> getTypeLikeName(@RequestBody ProductTypeDTO productTypeDTO) {
        ProductType productType = buildBO(productTypeDTO);
        PageInfo<ProductType> result = productTypeService.getLikeName(productType);
        return ResponseUtil.success(PageUtils.getPageInfo(result,result.getList()
                .stream()
                .map(ProductType::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/getByName")
    public Response<ProductTypeDTO> getTypeByName(@RequestBody ProductTypeDTO productTypeDTO) {
        ProductType productType = buildBO(productTypeDTO);
        ProductType result = productTypeService.getByName(productType);
        return ResponseUtil.success(result != null ? result.doBackward() : null);
    }

    @PostMapping("/delete")
    public Response remove(@RequestBody ProductTypeDTO productTypeDTO) {
        ProductType productType = buildBO(productTypeDTO);
        productTypeService.removeType(productType);
        return ResponseUtil.success();
    }

    @PostMapping("/update")
    public Response update(@RequestBody ProductTypeDTO productTypeDTO) {
        ProductType productType = buildBO(productTypeDTO);
        int result = productTypeService.updateType(productType);
        return SqlResultUtil.updateResult(result);
    }

    private ProductType buildBO(ProductTypeDTO productTypeDTO) {
        return BOUtils.convert(ProductType.class, productTypeDTO);
    }

}
