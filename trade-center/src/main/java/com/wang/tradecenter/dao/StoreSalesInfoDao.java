package com.wang.tradecenter.dao;

import com.wang.tradecenter.entity.PO.StoreSalesInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/27 16:15
 * @Description:
 */
@Mapper
public interface StoreSalesInfoDao {

    StoreSalesInfoPO getSalesToday(@Param("StoreId") String StoreId);

    int createSales(@Param("StoreId") String StoreId);

    int updateSales(@Param("storeSalesInfo")StoreSalesInfoPO storeSalesInfoPO);

    List<StoreSalesInfoPO> getRecentDateStoreSalesInfo(@Param("StoreId")String storeId,@Param("day")int day);

    List<StoreSalesInfoPO> getAdminRecentDateStoreSalesInfo(@Param("day")int day);
}
