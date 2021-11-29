package com.wang.productcenter.dao;

import com.wang.productcenter.entity.ProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther: wAnG
 * @Date: 2021/11/27 02:00
 * @Description:
 */

@Mapper
public interface ProductDao {

    ProductPO findAll(@Param("id") int id);
}
