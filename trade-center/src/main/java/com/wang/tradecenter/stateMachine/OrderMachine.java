package com.wang.tradecenter.stateMachine;

import com.wang.tradecenter.entity.BO.Order;

/**
 * @Auther: wAnG
 * @Date: 2022/3/22 22:03
 * @Description:
 */
public interface OrderMachine {

    void schedule(Order order,SendFunction function);

}
