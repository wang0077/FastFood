package com.wang.productcenter.service;

import com.github.pagehelper.PageInfo;
import com.wang.productcenter.entity.BO.Product;

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
}
