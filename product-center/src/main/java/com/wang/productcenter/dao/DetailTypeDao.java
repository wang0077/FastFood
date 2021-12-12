package com.wang.productcenter.dao;

import com.wang.productcenter.entity.PO.DetailTypePO;
import com.wang.productcenter.entity.PO.Product_DetailType_Middle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 01:44
 * @Description:
 */

@Mapper
public interface DetailTypeDao {

    List<DetailTypePO> getAll();

    int insert(@Param("detail_type")DetailTypePO detailTypePO);

    DetailTypePO getById(@Param("detail_type")DetailTypePO detailTypePO);

    List<DetailTypePO> getLikeName(@Param("detail_type")DetailTypePO detailTypePO);

    void remove(@Param("detail_type")DetailTypePO detailTypePO);

    int update(@Param("detail_type")DetailTypePO detailTypePO);

    DetailTypePO getByName(@Param("detail_type")DetailTypePO detailTypePO);

    List<DetailTypePO> getByProductDetailId(List<Integer> idList);

    List<Product_DetailType_Middle> getDetailTypeByProductId(List<Integer> idList);

    List<DetailTypePO> getByIds(List<Integer> idList);


}
