package com.wang.fastfood.usercenter.dao;

import com.wang.fastfood.usercenter.entity.PO.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 12:08
 * @Description:
 */

@Mapper
public interface UserDao {

    UserPO getByUserName(String userName);

    Integer add(@Param("User") UserPO userPO);

}
