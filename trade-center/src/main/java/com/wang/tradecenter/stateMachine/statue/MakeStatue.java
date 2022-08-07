package com.wang.tradecenter.stateMachine.statue;

import com.wang.fastfood.apicommons.entity.DTO.UpdateProductDetail;
import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Util.JSONUtil;
import com.wang.tradecenter.dao.OrderDao;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.entity.PO.OrderPO;
import com.wang.tradecenter.enums.OrderStatus;
import com.wang.tradecenter.fegin.ProductClient;
import com.wang.tradecenter.stateMachine.OrderMachine;
import com.wang.tradecenter.stateMachine.SendFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/31 18:43
 * @Description:
 */

@Service
public class MakeStatue implements OrderMachine {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AsyncRedis redisService;

    @Autowired
    private ProductClient productClient;

    private static final String ORDER_REDIS_NAME = "Order";

    private static final String REDIS_SEPARATE = "-";

    @Override
    public void schedule(Order order, SendFunction function) {
        order.setOrderStatus(OrderStatus.MAKE.getCode());
        String redisName = getOrderRedisName(order);
        redisService.set(redisName,order);
        String orderDetailJson = order.getOrderDetailJson();
        List<UpdateProductDetail> orderDetails = JSONUtil.parseToList(orderDetailJson, UpdateProductDetail.class);
        // 更新商品销量
        productClient.updateSales(orderDetails);
        OrderPO orderPO = order.doForward();
        orderDao.changeStatue(order.getOrderId(),order.getOrderStatus());
        function.Send(order);
    }

    private String getOrderRedisName(Order order){
        return ORDER_REDIS_NAME + REDIS_SEPARATE + order.getOrderId();
    }}
