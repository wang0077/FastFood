package com.wang.productcenter.dao;

import com.wang.fastfood.apicommons.entity.PO.ProductTypePO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 17:04
 * @Description:
 */

@Mapper
public interface ProductTypeDao {

    List<ProductTypePO> getAll();

}
