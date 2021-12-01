package com.wang.productcenter.dao;

import com.wang.productcenter.entity.PO.DetailTypePO;
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

    DetailTypePO getById(@Param("detail_type")DetailTypePO detailTypePO);

    List<DetailTypePO> getByName(@Param("detail_type")DetailTypePO detailTypePO);

    void remove(@Param("detail_type")DetailTypePO detailTypePO);

    void update(@Param("detail_type")DetailTypePO detailTypePO);

}
