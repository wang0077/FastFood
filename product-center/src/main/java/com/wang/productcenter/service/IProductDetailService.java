package com.wang.productcenter.service;

import com.wang.productcenter.entity.BO.ProductDetail;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 01:03
 * @Description:
 */
public interface IProductDetailService {

    List<ProductDetail> getAll();

    int insert(ProductDetail productDetail);

    ProductDetail getByName(ProductDetail productDetail);

    ProductDetail getById(ProductDetail productDetail);

    void remove(ProductDetail productDetail);

    List<ProductDetail> getLikeName(ProductDetail productDetail);

    int update(ProductDetail productDetail);

    Map<Integer,List<ProductDetail>> getByProductIds(List<Integer> idList);

}
