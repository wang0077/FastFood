package com.wang.tradecenter.dao;

import com.wang.tradecenter.entity.PO.ExceptionOrderPO;
import com.wang.tradecenter.entity.PO.OrderPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/25 15:34
 * @Description:
 */

@Mapper
public interface ExceptionOrderDao {

    Integer insertExceptionOrder(@Param("exceptionOrder") ExceptionOrderPO exceptionOrderPO);

    List<ExceptionOrderPO> getExceptionOrderByStoreId(@Param("exceptionOrder")ExceptionOrderPO exceptionOrderPO);

    Integer dealWithExceptionOrder(@Param("exceptionOrder") ExceptionOrderPO exceptionOrderPO);

    List<ExceptionOrderPO> searchExceptionOrder(@Param("order") OrderPO doForward);
}
