package com.wang.accountcenter.service;

import com.wang.accountcenter.entity.BO.SignIn;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 20:59
 * @Description:
 */
public interface ISignInService {

    SignIn getByUid(SignIn signIn);

    int insert(SignIn signIn);

    SignIn checkIn(SignIn signIn);

    SignIn checkInInfo(SignIn signIn);
}
