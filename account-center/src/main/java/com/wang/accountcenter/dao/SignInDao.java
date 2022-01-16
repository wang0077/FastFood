package com.wang.accountcenter.dao;

import com.wang.accountcenter.entity.PO.SignInPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 20:52
 * @Description:
 */

@Mapper
public interface SignInDao {

    SignInPO getByUid(@Param("signIn") SignInPO signInPO);

    int insert(@Param("signIn") SignInPO signInPO);

    int checkIn(@Param("signIn") SignInPO signInPO);

}
