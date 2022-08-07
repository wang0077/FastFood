package com.wang.fastfood.usercenter.service;

import com.wang.fastfood.usercenter.entity.BO.User;
import com.wang.fastfood.usercenter.exception.UserAlreadyExistException;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 11:59
 * @Description:
 */
public interface IUserService {

    User getByUserName(String userName);

    User getByUserId(String uid);

    List<User> getByUserIds(List<String> uidList);

    Integer add(User user) throws UserAlreadyExistException;

    Integer addStoreAdmin(User user);

    User getByStoreId(String storeId);

    User WXGetUserInfo(String openId);

    void WXSaveUserInfo(User user);

    Integer updatePassword(String userName,String oldPassword,String curPassword);
}
