package com.wang.tradecenter.stateMachine;

import com.wang.tradecenter.entity.BO.Order;

/**
 * @Auther: wAnG
 * @Date: 2022/3/24 00:42
 * @Description:
 */

@FunctionalInterface
public interface SendFunction {

    void Send(Order order);

}
