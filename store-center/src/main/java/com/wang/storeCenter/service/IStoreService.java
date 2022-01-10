package com.wang.storeCenter.service;

import com.github.pagehelper.PageInfo;
import com.wang.storeCenter.entity.BO.Store;
import com.wang.storeCenter.entity.BO.StoreRadius;
import redis.clients.jedis.GeoCoordinate;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/1/8 17:06
 * @Description:
 */
public interface IStoreService {

    PageInfo<Store> getAll(Store store);

    int insert(Store store);

    Store getByName(Store store);

    Store getById(Store store);

    List<Store> getByIds(List<Integer> idList);

    PageInfo<Store> getLikeName(Store store);

    int update(Store store);

    int remove(Store store);

    List<StoreRadius> storeRadius(GeoCoordinate geoCoordinate, double radius);

}
