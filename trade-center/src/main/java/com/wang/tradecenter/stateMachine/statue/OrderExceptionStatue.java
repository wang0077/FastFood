package com.wang.tradecenter.stateMachine.statue;

import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.tradecenter.dao.OrderDao;
import com.wang.tradecenter.entity.BO.ExceptionOrder;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.enums.OrderExceptionEnum;
import com.wang.tradecenter.enums.OrderStatus;
import com.wang.tradecenter.service.IExceptionOrderService;
import com.wang.tradecenter.stateMachine.OrderMachine;
import com.wang.tradecenter.stateMachine.SendFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

/**
 * @Auther: wAnG
 * @Date: 2022/4/25 20:31
 * @Description:
 */

@Service
public class OrderExceptionStatue implements OrderMachine {

    private static final String ORDER_REDIS_NAME = "Order";

    private static final String REDIS_SEPARATE = "-";


    private static final String STORE = "store";

    private static final String ORDER_RANKING = "order-ranking";

    @Autowired
    private IExceptionOrderService exceptionOrderService;

    @Autowired
    private OrderDao orderDao;


    @Override
    public void schedule(Order order, SendFunction function) {
        Integer orderStatus = order.getOrderStatus();
        OrderExceptionEnum exceptionEnum = null;
        // 超时关单
        if(OrderStatus.NOTPAY.getCode().equals(orderStatus)){
            exceptionEnum = OrderExceptionEnum.NO_PAY_TIME_OUT;
        }else if (!OrderStatus.NOTPAY.getCode().equals(orderStatus) && order.getTakeOrderNumber() == null){
            // 未得到取餐码
            exceptionEnum = OrderExceptionEnum.TAKE_CODE_EXCEPTION;
        }else if (OrderStatus.PAY_SUCCESS.getCode().equals(orderStatus)){
            exceptionEnum = OrderExceptionEnum.NO_MAKE_TIME_OUT;
        } else if (OrderStatus.PLEASE_PICK_UP.getCode().equals(orderStatus)){
            exceptionEnum = OrderExceptionEnum.PICK_UP_TIME_OUT;
        }else {
            exceptionEnum = OrderExceptionEnum.UNKNOWN_EXCEPTION;
        }
        ExceptionOrder exceptionOrder = buildExceptionOrder(order, exceptionEnum);
        orderDao.changeStatue(exceptionOrder.getOrderId(),OrderStatus.ORDER_REFUND_ABNORMAL.getCode());
        Integer result = exceptionOrderService.insertExceptionOrder(exceptionOrder);
        RedisUtil.del(getOrderRedisName(order));
        String rankingRedisName = getOrderRankingRedisName(order);
        RedisUtil.zremrangeByScore(rankingRedisName,order.getOrderTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
    }

    private ExceptionOrder buildExceptionOrder(Order order,OrderExceptionEnum exceptionEnum){
        ExceptionOrder exceptionOrder = new ExceptionOrder();
        exceptionOrder.setExceptionInfo(exceptionEnum.getType());
        exceptionOrder.setStoreId(Integer.valueOf(order.getStoreId()));
        exceptionOrder.setOrderId(order.getOrderId());
        exceptionOrder.setUid(order.getUid());
        exceptionOrder.setOrderAmount(order.getOrderAmount());
        exceptionOrder.setTakeMethod(order.getTakeMethod());
        return exceptionOrder;
    }

    private String getOrderRedisName(Order order){
        return ORDER_REDIS_NAME + REDIS_SEPARATE + order.getOrderId();
    }

    private String getOrderRankingRedisName(Order order){
        return STORE + REDIS_SEPARATE + order.getStoreId() + REDIS_SEPARATE + ORDER_RANKING;
    }
}
