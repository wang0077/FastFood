package com.wang.productcenter.service;

import com.github.pagehelper.PageInfo;
import com.wang.productcenter.entity.BO.ProductType;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 20:59
 * @Description:
 */
public interface IProductTypeService {

    PageInfo<ProductType> getAll(ProductType productType);

    int insert(ProductType productType);

    ProductType getById(ProductType productType);

    ProductType getByName(ProductType productType);

    PageInfo<ProductType> getLikeName(ProductType productType);

    void removeType(ProductType productType);

    int updateType(ProductType productType);

    Map<Integer,ProductType> groupById(List<Integer> idList);

    void flush();
}
