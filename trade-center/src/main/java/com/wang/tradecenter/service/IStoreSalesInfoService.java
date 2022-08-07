package com.wang.tradecenter.service;

import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.entity.BO.StoreSalesInfo;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/27 16:27
 * @Description:
 */

public interface IStoreSalesInfoService {

    int insertOrderSales(Order order);

    List<StoreSalesInfo> storeSalesInfoSevenDay(String storeId);

    List<StoreSalesInfo> adminStoreSalesInfoDays();

}
