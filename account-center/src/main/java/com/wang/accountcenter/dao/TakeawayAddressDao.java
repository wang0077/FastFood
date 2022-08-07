package com.wang.accountcenter.dao;

import com.wang.accountcenter.entity.PO.TakeawayAddressPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/12 02:36
 * @Description:
 */

@Mapper
public interface TakeawayAddressDao {

    void insert(@Param("takeawayAddress") TakeawayAddressPO takeawayAddressPO);

    void update(@Param("takeawayAddress") TakeawayAddressPO takeawayAddressPO);

    List<TakeawayAddressPO> getByUid(@Param("uid") String openId);

}
