package com.wang.productcenter.service;

import com.wang.fastfood.apicommons.entity.BO.ProductType;
import com.wang.fastfood.apicommons.entity.PO.ProductTypePO;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 20:59
 * @Description:
 */
public interface IProductTypeService {

    public List<ProductType> getAll();
}
