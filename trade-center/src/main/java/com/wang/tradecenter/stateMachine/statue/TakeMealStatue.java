package com.wang.tradecenter.stateMachine.statue;

import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.entity.request.CalculateInterestRequest;
import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.tradecenter.dao.OrderDao;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.entity.PO.OrderPO;
import com.wang.tradecenter.enums.OrderStatus;
import com.wang.tradecenter.exception.CalculateInterestException;
import com.wang.tradecenter.fegin.UserAccountClient;
import com.wang.tradecenter.stateMachine.OrderMachine;
import com.wang.tradecenter.stateMachine.SendFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Auther: wAnG
 * @Date: 2022/3/31 20:04
 * @Description:
 */

@Service
public class TakeMealStatue implements OrderMachine {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AsyncRedis redisService;

    @Autowired
    private UserAccountClient userAccountClient;

    private static final String ORDER_REDIS_NAME = "Order";

    private static final String REDIS_SEPARATE = "-";

    @Override
    public void schedule(Order order, SendFunction function) {
        order.setOrderStatus(OrderStatus.PLEASE_PICK_UP.getCode());
        order.setCompleteProductionTime(LocalDateTime.now());
        String redisName = getOrderRedisName(order);
        redisService.set(redisName,order);
        CalculateInterestRequest request = buildRequest(order);
        Response<String> response = userAccountClient.calculateOrderInterest(request);
        if(response.getCode() >= 20000){
            throw new CalculateInterestException("用户权益计算异常");
        }
        OrderPO orderPO = order.doForward();
        orderDao.changeStatueAndCompleteTime(order.getOrderId(),order.getOrderStatus(),order.getCompleteProductionTime());
        function.Send(order);
    }

    private CalculateInterestRequest buildRequest(Order order){
        CalculateInterestRequest request = new CalculateInterestRequest();
        request.setAmount(order.getOrderAmount());
        request.setUid(order.getUid());
        return request;
    }

    private String getOrderRedisName(Order order){
        return ORDER_REDIS_NAME + REDIS_SEPARATE + order.getOrderId();
    }
}

