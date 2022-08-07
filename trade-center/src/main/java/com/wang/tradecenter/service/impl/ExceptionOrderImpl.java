package com.wang.tradecenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.entity.response.UserInfoResponse;
import com.wang.tradecenter.dao.ExceptionOrderDao;
import com.wang.tradecenter.dao.OrderDao;
import com.wang.tradecenter.entity.BO.ExceptionOrder;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.entity.PO.ExceptionOrderPO;
import com.wang.tradecenter.enums.OrderStatus;
import com.wang.tradecenter.fegin.UserClient;
import com.wang.tradecenter.service.IExceptionOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/4/25 21:01
 * @Description:
 */

@Service
public class ExceptionOrderImpl implements IExceptionOrderService {

    @Autowired
    private ExceptionOrderDao exceptionOrderDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private UserClient userClient;

    public Integer insertExceptionOrder(ExceptionOrder exceptionOrder){
        ExceptionOrderPO exceptionOrderPO = exceptionOrder.doForward();
        return exceptionOrderDao.insertExceptionOrder(exceptionOrderPO);
    }

    @Override
    public PageInfo<ExceptionOrder> getExceptionOrderByStoreId(ExceptionOrder exceptionOrder) {
        ExceptionOrderPO exceptionOrderPO = exceptionOrder.doForward();
        if(exceptionOrder.IsPage()){
            PageUtils.startPage(exceptionOrder);
        }
        List<ExceptionOrderPO> exceptionOrderPOs = exceptionOrderDao.getExceptionOrderByStoreId(exceptionOrderPO);
        List<ExceptionOrder> result = exceptionOrderPOs.stream()
                .map(ExceptionOrderPO::convertToOrder)
                .collect(Collectors.toList());

        List<String> orderIds = result.stream()
                .map(ExceptionOrder::getOrderId)
                .distinct()
                .collect(Collectors.toList());

        if(orderIds.size() > 0){
            List<Order> orders = orderService.getByOrderIds(orderIds);
            List<String> UserIds = orders.stream()
                    .map(Order::getUid)
                    .distinct()
                    .collect(Collectors.toList());
            Response<List<UserInfoResponse>> response = userClient.getUserByUidList(UserIds);
            if(response.getCode() == 10000 && response.getData() != null){
                List<UserInfoResponse> userList = response.getData();
                result.forEach(exceptionOrderItem -> {
                    userList.forEach(user -> {
                        if (user.getUid().equals(exceptionOrderItem.getUid())){
                            exceptionOrderItem.setUserName(user.getUsername());
                            exceptionOrderItem.setPhoneNumber(user.getPhoneNumber());
                        }
                    });
                });
            }
            result.forEach(exceptionOrderItem -> {
                orders.forEach(order -> {
                    if (order.getOrderId().equals(exceptionOrderItem.getOrderId())){
                        exceptionOrderItem.setOrderDetailJSON(order.getOrderDetailJson());
                        exceptionOrderItem.setOrderTime(order.getOrderTime());
                        exceptionOrderItem.setTakeOrderNumber(order.getTakeOrderNumber());
                    }
                });
            });
        }
        return PageUtils.getPageInfo(exceptionOrderPOs,result);
    }

    @Override
    public Integer dealWithExceptionOrder(ExceptionOrder exceptionOrder) {
        orderDao.changeStatue(exceptionOrder.getOrderId(), OrderStatus.ORDER_SUCCESS.getCode());
        return exceptionOrderDao.dealWithExceptionOrder(exceptionOrder.doForward());
    }

    @Override
    public PageInfo<ExceptionOrder> searchExceptionOrder(Order order) {
        if(order.IsPage()){
            PageUtils.startPage(order);
        }
        List<ExceptionOrderPO> orderPOS = exceptionOrderDao.searchExceptionOrder(order.doForward());
        List<ExceptionOrder> orderList = orderPOS.stream()
                .map(ExceptionOrderPO::convertToOrder)
                .collect(Collectors.toList());
        return PageUtils.getPageInfo(orderPOS,orderList);
    }

}
