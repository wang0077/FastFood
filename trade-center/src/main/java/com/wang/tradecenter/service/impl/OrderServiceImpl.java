package com.wang.tradecenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.IDUtil;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.DTO.DetailTypeDTO;
import com.wang.fastfood.apicommons.entity.DTO.OrderDetail;
import com.wang.fastfood.apicommons.entity.DTO.ProductDTO;
import com.wang.fastfood.apicommons.entity.DTO.StoreDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.entity.request.PayRequest;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.exception.RemoteAccessException;
import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Util.JSONUtil;
import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.tradecenter.dao.OrderDao;
import com.wang.tradecenter.entity.BO.HistoryOrder;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.entity.BO.OrderDetailJson;
import com.wang.tradecenter.entity.PO.OrderPO;
import com.wang.tradecenter.entity.response.CreateOrderResponse;
import com.wang.tradecenter.entity.response.OrderStatueResponse;
import com.wang.tradecenter.enums.OrderStatus;
import com.wang.tradecenter.enums.PayMethod;
import com.wang.tradecenter.exception.OrderNotFindException;
import com.wang.tradecenter.exception.OrderPayException;
import com.wang.tradecenter.fegin.ProductClient;
import com.wang.tradecenter.fegin.StoreClient;
import com.wang.tradecenter.fegin.UserAccountClient;
import com.wang.tradecenter.orderMQ.OrderMQ;
import com.wang.tradecenter.service.IOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/3/20 17:53
 * @Description:
 */

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private StoreClient storeClient;

    @Autowired
    private AsyncRedis redisService;

    @Autowired
    private OrderMQ orderMQ;

    @Autowired
    private UserAccountClient userAccountClient;

    private static final String STORE = "store";

    private static final String ORDER_RANKING = "order-ranking";

    private static final String ORDER_REDIS_NAME = "Order";

    private static final String REDIS_SEPARATE = "-";

    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    @Override
    public CreateOrderResponse createOrder(Order order) {
        computeAmountAndDetailJson(order);
        order.setOrderId("O" + IDUtil.getID());
        LocalDateTime now = LocalDateTime.now();
        order.setOrderStatus(OrderStatus.NOTPAY.getCode());
        order.setOrderTime(now);
        OrderPO orderPO = order.doForward();
        int result = orderDao.createOrder(orderPO);
        if(result > 0){
            String redisName = getOrderRedisName(order);
            redisService.set(redisName,order);
            orderMQ.sendLeveOne(order);
        }
        CreateOrderResponse response = new CreateOrderResponse();
        response.setOrderId(order.getOrderId());
        response.setAmount(order.getOrderAmount());
        return response;
    }

    @Override
    public CodeEnum pay(Order order) {
        String orderId = order.getOrderId();
        OrderPO result = orderDao.getOrderByOrderId(orderId);
        if(result == null){
            throw new OrderNotFindException("订单不存在,订单存在异常");
        }
        if(Objects.equals(order.getPayMethod(), PayMethod.FASTFOOD_PAY.getCode())){
            PayRequest payRequest = buildPayRequest(result);
            Response response = userAccountClient.pay(payRequest);
            if(response.getCode() != CodeEnum.SUCCESS.getCode()){
                throw new OrderPayException("支付异常");
            }
        }
        int payResult = orderDao.addOrderPayMethod(orderId, order.getPayMethod());
        Order oldOrder = result.convertToOrder();
        oldOrder.setOrderStatus(OrderStatus.PAY_SUCCESS.getCode());
        oldOrder.setPayMethod(order.getPayMethod());
        String redisName = getOrderRedisName(order);
        redisService.set(redisName,oldOrder);
        return CodeEnum.PAY_SUCCESS;
    }

    @Override
    public void make(Order order) {
        String orderId = order.getOrderId();
        OrderPO result = orderDao.getOrderByOrderId(orderId);
        if(result == null){
            throw new OrderNotFindException("订单不存在,订单存在异常");
        }
        Order oldOrder = result.convertToOrder();
        oldOrder.setOrderStatus(OrderStatus.MAKE.getCode());
        String redisName = getOrderRedisName(order);
        redisService.set(redisName,oldOrder);
    }

    @Override
    public void take(Order order) {
        String orderId = order.getOrderId();
        OrderPO result = orderDao.getOrderByOrderId(orderId);
        if(result == null){
            throw new OrderNotFindException("订单不存在,订单存在异常");
        }
        Order oldOrder = result.convertToOrder();
        oldOrder.setOrderStatus(OrderStatus.PLEASE_PICK_UP.getCode());
        String redisName = getOrderRedisName(order);
        redisService.set(redisName,oldOrder);
    }

    @Override
    public void OrderSuccess(Order order) {
        String orderId = order.getOrderId();
        OrderPO result = orderDao.getOrderByOrderId(orderId);
        if(result == null){
            throw new OrderNotFindException("订单不存在,订单存在异常");
        }
        Order oldOrder = result.convertToOrder();
        oldOrder.setOrderStatus(OrderStatus.ORDER_SUCCESS.getCode());
        String redisName = getOrderRedisName(order);
        redisService.set(redisName,oldOrder);
    }

    @Override
    public Order getOrderInfo(Order order) {
        Order result = null;
        String orderId = order.getOrderId();
        String redisName = getOrderRedisName(order);
        result = RedisUtil.get(redisName,Order.class);
        if(result == null){
            OrderPO resultPO = orderDao.getOrderByOrderId(orderId);
            result = resultPO.convertToOrder();
        }
        return result;
    }

    @Override
    public Integer getTakeOrderNumber(Order order) {
        Order orderInfo = getOrderInfo(order);
        return orderInfo.getTakeOrderNumber();
    }

    @Override
    public OrderStatueResponse getOrderStatue(Order order) {
        OrderStatueResponse response = new OrderStatueResponse();
        Order orderInfo = getOrderInfo(order);
        response.setOrderStatueCode(orderInfo.getOrderStatus());
        String time = null;
        if(orderInfo.getCompleteProductionTime() != null){
            time = orderInfo.getCompleteProductionTime().format(format).replace("T"," ");
        }
        response.setCompleteTime(time);
        return response;
    }

    @Override
    public List<HistoryOrder> getHistoryOrderInfo(Order order) {
        String openId = order.getUid();
        List<OrderPO> orderInfoPO = orderDao.getHistoryOrderInfoByUid(openId);
        List<Order> orderInfos = orderInfoPO.stream()
                .map(OrderPO::convertToOrder)
                .collect(Collectors.toList());
        List<HistoryOrder> historyOrders = buildHistoryOrder(orderInfos);

        // 获取对应门店信息
        List<Integer> storeIds = historyOrders.stream()
                .map(orderInfo -> Integer.valueOf(orderInfo.getStoreId()))
                .distinct()
                .collect(Collectors.toList());

        Response<List<StoreDTO>> response = storeClient.getByIds(storeIds);
        List<StoreDTO> storeInfo;
        if(response.getCode() == CodeEnum.SUCCESS.getCode() && response.getData() != null){
            storeInfo = response.getData();
        }else {
            throw new RemoteAccessException();
        }
        buildHistoryOrderWithStoreInfo(historyOrders,storeInfo);
        return historyOrders;
    }

    @Override
    public PageInfo<Order> getOrderProgressByStoreId(Order order,List<Integer> selectStatue) {
        OrderPO orderPO = order.doForward();
        if(order.IsPage()){
            PageUtils.startPage(order);
        }
        List<OrderPO> orderInfo = orderDao.getOrderProgressByStoreId(orderPO,selectStatue);
        List<Order> orders = orderInfo.stream().map(OrderPO::convertToOrder).collect(Collectors.toList());
        return PageUtils.getPageInfo(orderInfo,orders);
    }

    @Override
    public PageInfo<Order> getTakeStatueOrderByStoreId(Order order) {
        OrderPO orderPO = order.doForward();
        if(order.IsPage()){
            PageUtils.startPage(order);
        }
        List<OrderPO> orderInfo = orderDao.getTakeStatueOrderByStoreId(orderPO);
        List<Order> orders = orderInfo.stream().map(OrderPO::convertToOrder).collect(Collectors.toList());
        return PageUtils.getPageInfo(orderInfo,orders);
    }

    @Override
    public List<Order> getByOrderIds(List<String> OrderIds) {
        List<OrderPO> orderPO = orderDao.getByOrderIds(OrderIds);
        return orderPO.stream().map(OrderPO::convertToOrder).collect(Collectors.toList());
    }

    @Override
    public PageInfo<Order> getStoreHistoryOderInfo(Order order) {
        OrderPO orderPO = order.doForward();
        if (order.IsPage()){
            PageUtils.startPage(order);
        }
        List<OrderPO> orderPOS = orderDao.getStoreHistoryOderInfo(orderPO);
        List<Order> result = orderPOS.stream()
                .map(OrderPO::convertToOrder)
                .collect(Collectors.toList());
        return PageUtils.getPageInfo(orderPOS,result);
    }

    @Override
    public Long OrderRanking(Order order) {
        String redisName = getOrderRankingRedisName(order);
        return RedisUtil.zadd(redisName,order.getOrderTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli(),order.getOrderTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
    }

    @Override
    public Long getOrderRankingCount(String storeId) {
        String rankingRedisName = getOrderRankingRedisName(storeId);
        return RedisUtil.zcard(rankingRedisName);
    }

    public Long userOrderRanking(Order order){
        String orderId = order.getOrderId();
        String storeId = order.getStoreId();
        String orderRankingRedisName = getOrderRankingRedisName(storeId);
        String orderRedisName = getOrderRedisName(order);
        Long totalNumber = RedisUtil.zcard(orderRankingRedisName);
        Order rankOrderInfo = RedisUtil.get(orderRedisName, Order.class);
        long time = rankOrderInfo.getOrderTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        // 由于zSet是倒序排序，需要总数 - 1 - 当前排名得到才是正向的排名
        return totalNumber - 1 - RedisUtil.zrevrank(orderRankingRedisName, time);
    }

    @Override
    public PageInfo<Order> searchProgressOrder(Order order) {
        if(order.IsPage()){
            PageUtils.startPage(order);
        }
        List<OrderPO> orderPOS = orderDao.searchProgressOrder(order.doForward());
        List<Order> orderList = orderPOS.stream()
                .map(OrderPO::convertToOrder)
                .collect(Collectors.toList());
        return PageUtils.getPageInfo(orderPOS,orderList);
    }

    @Override
    public PageInfo<Order> searchHistoryOrder(Order order) {
        if(order.IsPage()){
            PageUtils.startPage(order);
        }
        List<OrderPO> orderPOS = orderDao.searchHistoryOrder(order.doForward());
        List<Order> orderList = orderPOS.stream()
                .map(OrderPO::convertToOrder)
                .collect(Collectors.toList());
        return PageUtils.getPageInfo(orderPOS,orderList);
    }


    private String getOrderRankingRedisName(Order order){
        return getOrderRankingRedisName(order.getStoreId());
    }

    private String getOrderRankingRedisName(String orderId){
        return STORE + REDIS_SEPARATE + orderId + REDIS_SEPARATE + ORDER_RANKING;
    }

    private PayRequest buildPayRequest(OrderPO order){
        PayRequest payRequest = new PayRequest();
        payRequest.setUid(order.getUid());
        payRequest.setAmount(order.getOrderAmount());
        return payRequest;
    }

    public void buildHistoryOrderWithStoreInfo(List<HistoryOrder> historyOrderList,List<StoreDTO> storeList){
        storeList.forEach(store -> {
            historyOrderList.forEach(historyOrder -> {
                if(historyOrder.getStoreId().equals(String.valueOf(store.getId()))){
                    historyOrder.setStoreName(store.getStoreName());
                    historyOrder.setStoreAddress(store.getAddress());
                }
            });
        });
    }

    private List<HistoryOrder> buildHistoryOrder(List<Order> orderList){
        List<HistoryOrder> historyOrders = new ArrayList<>();
        orderList.forEach(order -> {
            HistoryOrder historyOrder = new HistoryOrder();
            BeanUtils.copyProperties(order,historyOrder);
            historyOrder.setOrderTime(order.getOrderTime().toString().replace("T"," "));
            if(order.getCompleteProductionTime() != null){
                historyOrder.setCompleteProductionTime(order.getCompleteProductionTime().toString().replace("T"," "));
            }
            String orderStatusName = OrderStatus.getOrderStatusName(historyOrder.getOrderStatus());
            historyOrder.setOrderStatusName(orderStatusName);
            historyOrders.add(historyOrder);
        });
        return historyOrders;
    }

    private void computeAmountAndDetailJson(Order order){
        double amount = 0;
        List<OrderDetail> orderDetails = order.getOrderDetail();
        List<Integer> productIds = new ArrayList<>();
        List<Integer> detailTypeIds = new ArrayList<>();
        Map<Integer, DetailTypeDTO> detailTypeMap = null;
        Map<Integer, ProductDTO> productMap = null;
        List<OrderDetailJson> orderDetailJson = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails){
            productIds.add(orderDetail.getProductId());
            if(orderDetail.getDetailTypeIds() != null && orderDetail.getDetailTypeIds().size() > 0){
                detailTypeIds.addAll(orderDetail.getDetailTypeIds());
            }
        }
        productIds = productIds.stream().distinct().collect(Collectors.toList());
        detailTypeIds = detailTypeIds.stream().distinct().collect(Collectors.toList());
        Response<List<ProductDTO>> productResult = productClient.getProductByIds(productIds);
        if(productResult.getCode() == CodeEnum.SUCCESS.getCode() && productResult.getData() != null){
            List<ProductDTO> products = productResult.getData();
            productMap = products.stream()
                    .collect(Collectors.toMap(ProductDTO::getId, productDTO -> productDTO));
        }
        Response<List<DetailTypeDTO>> detailTypeResult = productClient.getDetailTypeByIds(detailTypeIds);
        if(detailTypeResult.getCode() == CodeEnum.SUCCESS.getCode() && detailTypeResult.getData() != null){
            List<DetailTypeDTO> detailTypes = detailTypeResult.getData();
            detailTypeMap = detailTypes.stream()
                    .collect(Collectors.toMap(DetailTypeDTO::getId, detailTypeDTO -> detailTypeDTO));
        }
        for(OrderDetail orderDetail : orderDetails){
            if(productMap != null){
                ProductDTO productDTO = productMap.get(orderDetail.getProductId());
                amount += productDTO.getProductPrice() * orderDetail.getNumber();
                OrderDetailJson itemJson = new OrderDetailJson();
                itemJson.setNumber(orderDetail.getNumber());
                itemJson.setPrice(productDTO.getProductPrice());
                itemJson.setProductName(productDTO.getProductName());
                if(orderDetail.getDetailTypeIds() != null && detailTypeMap != null){
                    for(Integer detailTypeId : orderDetail.getDetailTypeIds()){
                        DetailTypeDTO detailTypeDTO = detailTypeMap.get(detailTypeId);
                        amount += detailTypeDTO.getPrice();
                        itemJson.setPrice(detailTypeDTO.getPrice());
                        itemJson.addProductDetailName(detailTypeDTO.getDetailTypeName());
                    }
                }
                orderDetailJson.add(itemJson);
            }
        }
        order.setOrderDetailJson(JSONUtil.toJsonString(orderDetailJson));
        order.setOrderAmount(amount);
    }

    private String getOrderRedisName(Order order){
        return ORDER_REDIS_NAME + REDIS_SEPARATE + order.getOrderId();
    }
}
