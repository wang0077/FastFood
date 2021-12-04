package com.wang.productcenter.dao;

import com.wang.productcenter.entity.PO.ProductPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/11/27 02:00
 * @Description:
 */

@Mapper
public interface ProductDao {

    List<ProductPO> getAll();
}
