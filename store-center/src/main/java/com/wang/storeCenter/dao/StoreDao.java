package com.wang.storeCenter.dao;

import com.wang.storeCenter.entity.PO.StorePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单表
 *
 * @author wAnG
 * @date 2022-01-08 15:25:15
 */
@Mapper
public interface StoreDao {

    List<StorePO> getStoreAll();

    int add(@Param("store") StorePO storePO);

    StorePO getById(@Param("store") StorePO storePO);

    StorePO getByName(@Param("store") StorePO storePO);

    List<StorePO> getLikeName(@Param("store") StorePO storePO);

    int update(@Param("store") StorePO storePO);

    int remove(@Param("store") StorePO storePO);

    List<StorePO> getByIds(List<Integer> idList);

}
