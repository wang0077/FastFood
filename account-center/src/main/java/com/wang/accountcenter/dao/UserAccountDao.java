package com.wang.accountcenter.dao;

import com.wang.accountcenter.entity.PO.UserAccountPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 16:46
 * @Description:
 */

@Mapper
public interface UserAccountDao {

    UserAccountPO getByUserId(@Param("userAccount") UserAccountPO userAccountPO);

    int insert(@Param("userAccount") UserAccountPO userAccountPO);

    int checkIn(@Param("userAccount") UserAccountPO userAccountPO);

    double getUserAmount(@Param("uid")String openId);

    void rechargeAmount(@Param("userAccount") UserAccountPO userAccountPO);

    int pay(@Param("uid")String openId,@Param("amount")Double amount);

    int addExperienceAndIntegral(@Param("userAccount")UserAccountPO userAccountPO);

}
