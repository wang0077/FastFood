package com.wang.tradecenter.stateMachine.statue;

import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.tradecenter.dao.OrderDao;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.entity.PO.OrderPO;
import com.wang.tradecenter.stateMachine.OrderMachine;
import com.wang.tradecenter.stateMachine.SendFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: wAnG
 * @Date: 2022/3/23 21:07
 * @Description: 状态变为超时关单
 */

@Service
public class CloseOrderStatue implements OrderMachine {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AsyncRedis redisService;

    private static final String ORDER_REDIS_NAME = "Order";

    private static final String REDIS_SEPARATE = "-";

    @Override
    public void schedule(Order order, SendFunction sendFunction) {
        OrderPO orderPO = order.doForward();
        int result = orderDao.updateByOrderId(orderPO);
        String redisName = getOrderRedisName(order);
        redisService.set(redisName,order);
    }

    private String getOrderRedisName(Order order){
        return ORDER_REDIS_NAME + REDIS_SEPARATE + order.getOrderId();
    }
}
