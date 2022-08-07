package com.wang.tradecenter.stateMachine.statue;

import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.tradecenter.dao.OrderDao;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.entity.PO.OrderPO;
import com.wang.tradecenter.enums.OrderStatus;
import com.wang.tradecenter.service.IStoreSalesInfoService;
import com.wang.tradecenter.stateMachine.OrderMachine;
import com.wang.tradecenter.stateMachine.SendFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/1 21:09
 * @Description:
 */

@Service
public class OrderSuccessStatue implements OrderMachine {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AsyncRedis redisService;

    @Autowired
    private IStoreSalesInfoService storeSalesInfoService;

    private static final String ORDER_REDIS_NAME = "Order";

    private static final String REDIS_SEPARATE = "-";

    private static final String STORE = "store";

    private static final String ORDER_RANKING = "order-ranking";

    @Override
    public void schedule(Order order, SendFunction function) {
        order.setOrderStatus(OrderStatus.ORDER_SUCCESS.getCode());
        String redisName = getOrderRedisName(order);
        List<String> keys = RedisUtil.keys(redisName);
        if(keys != null && keys.size() > 0){
            RedisUtil.del(redisName);
        }
        OrderPO orderPO = order.doForward();
        orderDao.changeStatue(order.getOrderId(),order.getOrderStatus());
        storeSalesInfoService.insertOrderSales(order);
        String rankingRedisName = getOrderRankingRedisName(order);
        RedisUtil.zremrangeByScore(rankingRedisName,order.getOrderTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
    }

    private String getOrderRedisName(Order order){
        return ORDER_REDIS_NAME + REDIS_SEPARATE + order.getOrderId();
    }

    private String getOrderRankingRedisName(Order order){
        return STORE + REDIS_SEPARATE + order.getStoreId() + REDIS_SEPARATE + ORDER_RANKING;
    }
}
