package com.wang.tradecenter.stateMachine.statue;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.entity.DTO.StoreDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.tradecenter.dao.OrderDao;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.enums.OrderStatus;
import com.wang.tradecenter.fegin.ProductClient;
import com.wang.tradecenter.fegin.StoreClient;
import com.wang.tradecenter.orderMQ.OrderMQ;
import com.wang.tradecenter.service.IOrderService;
import com.wang.tradecenter.stateMachine.OrderMachine;
import com.wang.tradecenter.stateMachine.SendFunction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/3/22 22:04
 * @Description: 状态变为支付成功
 */

@Service
public class PayStatue implements OrderMachine, InitializingBean {

    @Autowired
    private ProductClient productClient;

    @Autowired
    private StoreClient storeClient;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderMQ orderMQ;

    @Autowired
    private AsyncRedis redisServer;

    @Autowired
    private IOrderService orderService;

    private static final String ORDER_REDIS_NAME = "Order";

    private static final String REDIS_SEPARATE = "-";

    private static final String REDIS_STORE_TAKE_NUMBER = "Store-Take-Number-";

    private final Map<String,String> StoreNameHash = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        Response<PageInfo<StoreDTO>> response = storeClient.getStoreAll(new StoreDTO());
        if(response.getCode() == CodeEnum.SUCCESS.getCode() && response.getData().getList() != null){
            List<StoreDTO> storeList = response.getData().getList();
            List<Long> storeIds = storeList.stream()
                    .map(StoreDTO::getId)
                    .collect(Collectors.toList());
            List<String> redisNames = storeIds.stream()
                    .map(id -> getRedisNameStoreTakeNumber(id.toString()))
                    .collect(Collectors.toList());
            if(redisNames.size() > 0){
                storeList.forEach(storeDTO -> {
                    StoreNameHash.put(storeDTO.getId().toString(),storeDTO.getId().toString());
                });
            }
        }
    }

    @Override
    public void schedule(Order order, SendFunction sendFunction) {
        String storeId = order.getStoreId();
        String redisName = StoreNameHash.get(storeId);
        while(storeId.equals(redisName)){
            synchronized (StoreNameHash.get(storeId)){
                if(!StoreNameHash.get(storeId).equals(storeId)){
                    break;
                }else{
                    redisName = getRedisNameStoreTakeNumber(storeId);
                    StoreNameHash.put(storeId,redisName);
                    RedisUtil.set(redisName,0);
                }
            }
        }
        Long number = RedisUtil.incr(redisName);
        if(number == 500){
            RedisUtil.set(redisName,0);
        }
        order.setTakeOrderNumber(number.intValue());
        order.setOrderStatus(OrderStatus.PAY_SUCCESS.getCode());
        String orderRedisName = getOrderRedisName(order);
        redisServer.set(orderRedisName,order);
        orderDao.changeStatueAndTakeNumber(order.getOrderId(),order.getOrderStatus(),order.getTakeOrderNumber());
        orderService.OrderRanking(order);
        sendFunction.Send(order);
    }

    public String getRedisNameStoreTakeNumber(String storeId){
        return REDIS_STORE_TAKE_NUMBER + storeId;
    }

    private String getOrderRedisName(Order order){
        return ORDER_REDIS_NAME + REDIS_SEPARATE + order.getOrderId();
    }


}
