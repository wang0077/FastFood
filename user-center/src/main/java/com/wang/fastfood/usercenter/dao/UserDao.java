package com.wang.fastfood.usercenter.dao;

import com.wang.fastfood.usercenter.entity.PO.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 12:08
 * @Description:
 */

@Mapper
public interface UserDao {

    UserPO getByUserName(String userName);

    UserPO getByUserId(String uid);

    List<UserPO> getByUserIds(@Param("UidList") List<String> uidList);

    Integer add(@Param("User") UserPO userPO);

    Integer addAdmin(@Param("User")UserPO userPO);

    UserPO getByStoreId(@Param("storeId")String storeId);

    UserPO WXGetUserInfo(@Param("uid")String openId);

    void WXSaveUserInfo(@Param("User")UserPO userPO);

    Integer updatePassword(@Param("password")String password,@Param("userName")String userName);

}
