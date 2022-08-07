package com.wang.tradecenter.service;

import com.github.pagehelper.PageInfo;
import com.wang.tradecenter.entity.BO.ExceptionOrder;
import com.wang.tradecenter.entity.BO.Order;

/**
 * @Auther: wAnG
 * @Date: 2022/4/25 21:00
 * @Description:
 */

public interface IExceptionOrderService {

    Integer insertExceptionOrder(ExceptionOrder exceptionOrder);

    PageInfo<ExceptionOrder> getExceptionOrderByStoreId(ExceptionOrder exceptionOrder);

    Integer dealWithExceptionOrder(ExceptionOrder exceptionOrder);

    PageInfo<ExceptionOrder> searchExceptionOrder(Order order);

}
