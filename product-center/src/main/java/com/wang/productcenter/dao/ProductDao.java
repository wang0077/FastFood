package com.wang.productcenter.dao;

import com.wang.productcenter.entity.PO.ProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/11/27 02:00
 * @Description:
 */

@Mapper
public interface ProductDao {

    List<ProductPO> getAll();

    ProductPO getById(@Param("product")ProductPO productPO);

    ProductPO getByName(@Param("product")ProductPO productPO);

    List<ProductPO> likeByName(@Param("product")ProductPO productPO);

    void remove(@Param("product")ProductPO productPO);

    int update(@Param("product")ProductPO productP);
}
