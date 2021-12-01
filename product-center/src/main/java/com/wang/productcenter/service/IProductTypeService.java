package com.wang.productcenter.service;

import com.wang.productcenter.entity.BO.ProductType;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 20:59
 * @Description:
 */
public interface IProductTypeService {

    List<ProductType> getAll();

    ProductType getById(ProductType productType);

    List<ProductType> getByName(ProductType productType);

    void removeType(ProductType productType);

    void updateType(ProductType productType);
}
