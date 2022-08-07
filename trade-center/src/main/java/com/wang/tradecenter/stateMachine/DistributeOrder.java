package com.wang.tradecenter.stateMachine;

import com.wang.tradecenter.entity.BO.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: wAnG
 * @Date: 2022/3/23 21:48
 * @Description:
 */

@Service
public class DistributeOrder {

    @Autowired
    private OrderMachine noChangeStatue;

    @Autowired
    private OrderMachine closeOrderStatue;

    private static final int CLOSED = 10;

    @Autowired
    private OrderMachine payStatue;

    private static final int PAY_SUCCESS = 2;

    @Autowired
    private OrderMachine makeStatue;

    private static final int MAKE = 3;

    @Autowired
    private OrderMachine takeMealStatue;

    private static final int PLEASE_PICK_UP = 5;

    @Autowired
    private OrderMachine orderSuccessStatue;

    private static final int ORDER_SUCCESS = 7;

    @Autowired
    private OrderMachine orderExceptionStatue;

    public void chose(Order order,boolean isException){
        orderExceptionStatue.schedule(order,null);
    }

    // 处理状态发送改变过的订单
    public void chose(Order order,SendFunction sendFunction){
        OrderMachine orderMachine = switchTo(order);
        if(orderMachine == null){
            throw new NullPointerException();
        }
        orderMachine.schedule(order,sendFunction);
    }

    // 处理状态未发送改变的订单
    public void chose(Order order,SendFunction sendFunction,boolean isChange){
        if(!isChange){
            noChangeStatue.schedule(order,sendFunction);
        }else {
            chose(order,sendFunction);
        }
    }

    private OrderMachine switchTo(Order order){
        switch (order.getOrderStatus()){
            case PAY_SUCCESS :
                return payStatue;
            case CLOSED :
                return closeOrderStatue;
            case MAKE :
                return makeStatue;
            case PLEASE_PICK_UP :
                return takeMealStatue;
            case ORDER_SUCCESS:
                return orderSuccessStatue;
            default :
                return null;
        }
    }

}
