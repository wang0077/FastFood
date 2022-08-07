package com.wang.tradecenter.service.impl;

import com.wang.tradecenter.dao.StoreSalesInfoDao;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.entity.BO.StoreSalesInfo;
import com.wang.tradecenter.entity.PO.StoreSalesInfoPO;
import com.wang.tradecenter.enums.PayMethod;
import com.wang.tradecenter.enums.TakeMethod;
import com.wang.tradecenter.exception.CreateSalesException;
import com.wang.tradecenter.service.IStoreSalesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/4/27 16:28
 * @Description:
 */

@Service
public class StoreSalesInfoServiceImpl implements IStoreSalesInfoService {

    @Autowired
    private StoreSalesInfoDao storeSalesInfoDao;

    @Override
    public int insertOrderSales(Order order) {
        String storeId = order.getStoreId();
        StoreSalesInfoPO salesToday = storeSalesInfoDao.getSalesToday(storeId);
        if(salesToday == null){
            int result = storeSalesInfoDao.createSales(storeId);
            if(result == 0){
                throw new CreateSalesException("创建门店销量失败");
            }else {
                salesToday = storeSalesInfoDao.getSalesToday(storeId);
            }
        }
        buildSales(order,salesToday);
        return storeSalesInfoDao.updateSales(salesToday);
    }

    @Override
    public List<StoreSalesInfo> storeSalesInfoSevenDay(String storeId) {
        StoreSalesInfoPO salesToday = storeSalesInfoDao.getSalesToday(storeId);
        if(salesToday == null){
            storeSalesInfoDao.createSales(storeId);
        }
        List<StoreSalesInfoPO> storeSalesInfo = storeSalesInfoDao.getRecentDateStoreSalesInfo(storeId, 15);
        return storeSalesInfo.stream()
                .map(StoreSalesInfoPO::convertToStoreSalesInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<StoreSalesInfo> adminStoreSalesInfoDays() {
        List<StoreSalesInfoPO> salesInfo = storeSalesInfoDao.getAdminRecentDateStoreSalesInfo(15);
        Map<LocalDate, List<StoreSalesInfoPO>> salesInfoMap = salesInfo.stream()
                .collect(Collectors.groupingBy(storeSalesInfoPO -> storeSalesInfoPO.getCrateTime()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()));
        List<StoreSalesInfoPO> result = new ArrayList<>();
        salesInfoMap.forEach((key, value) -> {
            StoreSalesInfoPO salesInfoPO = value.get(0);
            value.stream().skip(1).forEach(storeSalesInfo -> {
                salesInfoPO.setSales(salesInfoPO.getSales() + storeSalesInfo.getSales());
                salesInfoPO.setDeliveryCount(salesInfoPO.getDeliveryCount() + storeSalesInfo.getDeliveryCount());
                salesInfoPO.setPickUpCount(salesInfoPO.getPickUpCount() + storeSalesInfo.getPickUpCount());
                salesInfoPO.setWxPayCount(salesInfoPO.getWxPayCount() + storeSalesInfo.getWxPayCount());
                salesInfoPO.setFastFoodPayCount(salesInfoPO.getFastFoodPayCount() + storeSalesInfo.getFastFoodPayCount());
            });
            result.add(salesInfoPO);
        });
        result.sort(Comparator.comparing(StoreSalesInfoPO::getCrateTime));
        return result.stream()
                .map(StoreSalesInfoPO::convertToStoreSalesInfo)
                .collect(Collectors.toList());
    }

    private void buildSales(Order order,StoreSalesInfoPO storeSalesInfo){
        storeSalesInfo.setSales(storeSalesInfo.getSales() + order.getOrderAmount());
        if(order.getTakeMethod().equals(TakeMethod.TAKE_IN.getCode())){
            storeSalesInfo.setPickUpCount(storeSalesInfo.getPickUpCount() + 1);
        }else {
            storeSalesInfo.setDeliveryCount(storeSalesInfo.getDeliveryCount() + 1);
        }
        if(order.getPayMethod().equals(PayMethod.FASTFOOD_PAY.getCode())){
            storeSalesInfo.setFastFoodPayCount(storeSalesInfo.getFastFoodPayCount() + 1);
        }else {
            storeSalesInfo.setWxPayCount(storeSalesInfo.getWxPayCount() + 1);
        }
    }
}
