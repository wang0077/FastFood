package com.wang.fastfood.usercenter.service;

import com.wang.fastfood.usercenter.entity.BO.User;
import com.wang.fastfood.usercenter.exception.UserAlreadyExistException;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 11:59
 * @Description:
 */
public interface IUserService {

    User getByUserName(String userName);

    Integer add(User user) throws UserAlreadyExistException;
}
