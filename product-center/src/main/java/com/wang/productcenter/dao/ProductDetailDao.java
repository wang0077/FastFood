package com.wang.productcenter.dao;

import com.wang.productcenter.entity.PO.ProductDetailPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 00:24
 * @Description:
 */

@Mapper
public interface ProductDetailDao {

    List<ProductDetailPO> getAll();

    int insert(@Param("product_detail") ProductDetailPO productDetailPO);

    ProductDetailPO getByName(@Param("product_detail") ProductDetailPO productDetailPO);

    ProductDetailPO getById(@Param("product_detail") ProductDetailPO productDetailPO);

    void remove(@Param("product_detail") ProductDetailPO productDetailPO);

    List<ProductDetailPO> getLikeName(@Param("product_detail") ProductDetailPO productDetailPO);

    int update(@Param("product_detail") ProductDetailPO productDetailPO);

}
