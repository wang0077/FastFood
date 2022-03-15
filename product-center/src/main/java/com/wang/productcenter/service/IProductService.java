package com.wang.productcenter.service;

import com.github.pagehelper.PageInfo;
import com.wang.productcenter.entity.BO.Product;
import com.wang.productcenter.entity.PO.Product_DetailType_Middle;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 20:57
 * @Description:
 */


public interface IProductService {

    PageInfo<Product> getAll(Product product);

    Product getById(Product product);

    Product getByName(Product product);

    PageInfo<Product> likeByName(Product product);

    void remove(Product product);

    int update(Product product);

    int insert(Product product);

    List<Product> getProductByTypeId(Integer id);

    int removeProductCacheByProductId(Integer id);

    int removeProductCacheByProductId(List<Integer> ProductIds);

    List<Integer> getProductIdsByTypeId(Integer typeId);

    List<Integer> getProductIdsByDetail(Integer productDetailId);

    List<Product_DetailType_Middle> getProductByDetailTypeId(List<Integer> detailTypeIds);

    public void flush();
}
