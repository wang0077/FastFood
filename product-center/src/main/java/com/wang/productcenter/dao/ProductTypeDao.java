package com.wang.productcenter.dao;

import com.wang.productcenter.entity.PO.ProductTypePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 17:04
 * @Description:
 */

@Mapper
public interface ProductTypeDao {

    List<ProductTypePO> getAll();

    ProductTypePO getById(@Param("productType") ProductTypePO productTypePO);

    List<ProductTypePO> getByName(@Param("productType") ProductTypePO productTypePO);

    void remove(@Param("productType") ProductTypePO productTypePO);

    void update(@Param("productType") ProductTypePO productTypePO);

}
