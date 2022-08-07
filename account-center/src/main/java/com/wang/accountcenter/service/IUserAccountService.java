package com.wang.accountcenter.service;

import com.wang.accountcenter.entity.BO.UserAccount;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 17:23
 * @Description:
 */

public interface IUserAccountService {

    int insert(UserAccount userAccount);

    UserAccount getByUserId(UserAccount userAccount);

    int checkIn(UserAccount userAccount);

    Double getUserAmount(String openId);

    void rechargeAmount(UserAccount userAccount);

    void pay(String OpenId,Double amount);

    Integer calculateOrderInterest(String openId, Double amount);
}
