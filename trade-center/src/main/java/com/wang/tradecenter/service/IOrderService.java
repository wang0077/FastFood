package com.wang.tradecenter.service;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.entity.response.CreateOrderResponse;
import com.wang.tradecenter.entity.BO.HistoryOrder;
import com.wang.tradecenter.entity.response.OrderStatueResponse;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/20 17:52
 * @Description:
 */
public interface IOrderService {

    CreateOrderResponse createOrder(Order order);

    CodeEnum pay(Order order);

    void make(Order order);

    void take(Order order);

    void OrderSuccess(Order order);

    Order getOrderInfo(Order order);

    Integer getTakeOrderNumber(Order order);

    OrderStatueResponse getOrderStatue(Order order);

    List<HistoryOrder> getHistoryOrderInfo(Order order);

    PageInfo<Order> getOrderProgressByStoreId(Order order, List<Integer> selectStatus);

    PageInfo<Order> getTakeStatueOrderByStoreId(Order order);

    List<Order> getByOrderIds(List<String> OrderIds);

    PageInfo<Order> getStoreHistoryOderInfo(Order order);

    Long OrderRanking(Order order);

    Long getOrderRankingCount(String storeId);

    Long userOrderRanking(Order order);

    PageInfo<Order> searchProgressOrder(Order order);

    PageInfo<Order> searchHistoryOrder(Order order);

}
