package com.wang.tradecenter.listener;

import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.enums.OrderStatus;
import com.wang.tradecenter.orderMQ.OrderMQ;
import com.wang.tradecenter.stateMachine.DistributeOrder;
import com.wang.tradecenter.stateMachine.SendFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Auther: wAnG
 * @Date: 2022/3/21 19:15
 * @Description:
 */

@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "Trade-Center-Group",topic = "OrderStatue")
public class OrderListener implements RocketMQListener<Order> {

    private static final String ORDER_REDIS_NAME = "Order";

    private static final String REDIS_SEPARATE = "-";


    @Autowired
    private DistributeOrder distributeOrder;

    @Autowired
    private OrderMQ orderMQ;

    @Override
    public void onMessage(Order order) {
        log.info("[Message : Consumer] ==> Order : [{}]",order);
        String redisName = getOrderRedisName(order);
        Order curOrder = RedisUtil.get(redisName, Order.class);
        if(curOrder == null){
            return;
        }
        LocalDateTime curTime = LocalDateTime.now();
        LocalDateTime oldTime = curOrder.getOrderTime();
        Duration between = Duration.between(oldTime,curTime);
        long betweenTime = between.toMinutes();
        SendFunction sendFunction = getMQLevel(betweenTime);
        // 如果订单存在异常
        if(sendFunction == null){
            distributeOrder.chose(curOrder,true);
            return;
        }
        // 判断是否未支付并超时
        if(curOrder.getOrderStatus().equals(OrderStatus.NOTPAY.getCode())){
            // 超时关单
            if(betweenTime >= 15){
                curOrder.setOrderStatus(OrderStatus.CLOSED.getCode());
                distributeOrder.chose(curOrder,sendFunction);
            }
        }
        // 判断状态是否有变更
        if(!order.getOrderStatus().equals(curOrder.getOrderStatus())){
            distributeOrder.chose(curOrder,sendFunction);
        }else {
            distributeOrder.chose(curOrder,sendFunction,false);
        }
    }

    private SendFunction getMQLevel(long betweenTime){
        SendFunction sendFunction = null;
        if(betweenTime < 1){
            sendFunction = (sendOrder) -> {
                orderMQ.sendLeveOne(sendOrder);
            };
        } else if(betweenTime < 2){
            sendFunction = (sendOrder) -> {
                orderMQ.sendLeveTwo(sendOrder);
            };
        } else if(betweenTime < 3){
            sendFunction = (sendOrder) -> {
                orderMQ.sendLeveThree(sendOrder);
            };
        }
        return sendFunction;
    }

    private String getOrderRedisName(Order order){
        return ORDER_REDIS_NAME + REDIS_SEPARATE + order.getOrderId();
    }
}
