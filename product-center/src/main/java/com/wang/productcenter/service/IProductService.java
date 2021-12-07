package com.wang.productcenter.service;

import com.wang.productcenter.entity.BO.Product;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 20:57
 * @Description:
 */


public interface IProductService {

    List<Product> getAll();

    Product getById(Product product);

    Product getByName(Product product);

    List<Product> likeByName(Product product);

    void remove(Product product);

    int update(Product product);
}
