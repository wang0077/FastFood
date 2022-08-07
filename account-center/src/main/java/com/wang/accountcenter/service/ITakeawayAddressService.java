package com.wang.accountcenter.service;

import com.wang.accountcenter.entity.BO.TakeawayAddress;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/12 02:54
 * @Description:
 */
public interface ITakeawayAddressService {

    void insert(TakeawayAddress takeawayAddress);

    List<TakeawayAddress> getByUid(String openId);
}
