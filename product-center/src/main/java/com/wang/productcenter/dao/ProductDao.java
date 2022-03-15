package com.wang.productcenter.dao;

import com.wang.productcenter.entity.PO.ProductPO;
import com.wang.productcenter.entity.PO.Product_DetailType_Middle;
import com.wang.productcenter.entity.PO.Product_Detail_Middle;
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

    List<ProductPO> getByIds(@Param("list")List<Integer> ids);

    ProductPO getByName(@Param("product")ProductPO productPO);

    List<ProductPO> likeByName(@Param("product")ProductPO productPO);

    void remove(@Param("product")ProductPO productPO);

    int update(@Param("product")ProductPO productPO);

    int insert(@Param("product")ProductPO productPO);

    int getProductIdByName(String productName);

    List<ProductPO> getProductByTypeId(@Param("id") Integer id);

    List<Integer> getProductIdByTypeId(@Param("id") Integer id);

    List<Product_DetailType_Middle> getProductByDetailTypeId(List<Integer> idList);

    List<Product_Detail_Middle> getProductByDetailId(@Param("list") List<Integer> productDetailIds);
}
