package com.wang.tradecenter.controller;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.ExceptionOrderDTO;
import com.wang.fastfood.apicommons.entity.DTO.OrderDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.tradecenter.entity.BO.ExceptionOrder;
import com.wang.tradecenter.entity.BO.HistoryOrder;
import com.wang.tradecenter.entity.BO.Order;
import com.wang.tradecenter.entity.request.*;
import com.wang.tradecenter.entity.response.CreateOrderResponse;
import com.wang.tradecenter.entity.response.OrderStatueResponse;
import com.wang.tradecenter.service.IExceptionOrderService;
import com.wang.tradecenter.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/3/20 17:36
 * @Description:
 */

@RestController
//@CrossOrigin
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IExceptionOrderService exceptionOrderService;

    @PostMapping("/createOrder")
    public Response<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request){
        Order order = buildOrder(request);
        CreateOrderResponse response = orderService.createOrder(order);
        return ResponseUtil.success(response);
    }

    @PostMapping("/pay")
    public Response<CodeEnum> pay(@RequestBody PayRequest request){
        Order order = buildOrder(request);
        CodeEnum payResult = orderService.pay(order);
        return ResponseUtil.success(payResult);
    }

    @PostMapping("/make")
    public Response make(@RequestBody makeOrderRequest makeOrderRequest){
        Order order = buildOrder(makeOrderRequest);
        orderService.make(order);
        return ResponseUtil.success();
    }

    @PostMapping("/takeMeal")
    public Response takeMeal(@RequestBody takeMealRequest takeMealRequest){
        Order order = buildOrder(takeMealRequest);
        orderService.take(order);
        return ResponseUtil.success();
    }

    @PostMapping("/orderSuccess")
    public Response orderSuccess(@RequestBody orderSuccessRequest orderSuccessRequest){
        Order order = buildOrder(orderSuccessRequest);
        orderService.OrderSuccess(order);
        return ResponseUtil.success();
    }

    @PostMapping("/getOrderInfo")
    public Response<OrderDTO> getOrderInfo(@RequestBody OrderInfoRequest request){
        Order order = buildOrder(request);
        Order result = orderService.getOrderInfo(order);
        return ResponseUtil.success(result.doBackward());
    }

    @PostMapping("/getOrderStatue")
    public Response<OrderStatueResponse> getOrderStatue(@RequestBody OrderInfoRequest request){
        Order order = buildOrder(request);
        OrderStatueResponse response = orderService.getOrderStatue(order);
        return ResponseUtil.success(response);
    }

    @PostMapping("/getOrderProgress")
    public Response<PageInfo<OrderDTO>> getOrderProgressByStoreId(@RequestBody OrderInfoRequest request){
        Order order = buildOrder(request);
        List<Integer> selectStatue = getSelectStatue(request);
        PageInfo<Order> orderInfo = orderService.getOrderProgressByStoreId(order,selectStatue);
        return ResponseUtil.success(PageUtils.getPageInfo(orderInfo,orderInfo.getList().stream()
                .map(Order::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/getTakeStatueOrder")
    public Response<PageInfo<OrderDTO>> getTakeStatueOrderByStoreId(@RequestBody OrderInfoRequest request){
        Order order = buildOrder(request);
        PageInfo<Order> orderInfo = orderService.getTakeStatueOrderByStoreId(order);
        return ResponseUtil.success(PageUtils.getPageInfo(orderInfo,orderInfo.getList().stream()
                .map(Order::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/getTakeOrderNumber")
    public Response<Integer> getTakeOrderNumber(@RequestBody OrderInfoRequest request){
        Order order = buildOrder(request);
        Integer number = orderService.getTakeOrderNumber(order);
        return ResponseUtil.success(number);
    }

    @PostMapping("/getHistoryOrder")
    public Response<List<HistoryOrder>> getHistoryOrderInfo(@RequestBody OrderInfoRequest request){
        Order order = buildOrder(request);
        List<HistoryOrder> historyOrderInfo = orderService.getHistoryOrderInfo(order);
        return ResponseUtil.success(historyOrderInfo);
    }

    @PostMapping("/getStoreHistoryOrder")
    public Response<PageInfo<OrderDTO>> getStoreHistoryOderInfo(@RequestBody OrderInfoRequest request){
        Order order = buildOrder(request);
        PageInfo<Order> result = orderService.getStoreHistoryOderInfo(order);
        return ResponseUtil.success(PageUtils.getPageInfo(result,result.getList()
                .stream()
                .map(Order::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/OrderRankingCount")
    public Response<Long> getOrderRankingCount(@RequestBody OrderDTO order){
        Long count = orderService.getOrderRankingCount(order.getStoreId());
        return ResponseUtil.success(count);
    }

    @PostMapping("/userOrderRanking")
    public Response<Long> userOrderRanking(@RequestBody OrderDTO orderDTO){
        Order order = buildBO(orderDTO);
        Long ranking = orderService.userOrderRanking(order);
        return ResponseUtil.success(ranking);
    }

    @PostMapping("/searchProgressOrder")
    public Response<PageInfo<OrderDTO>> searchProgressOrder(@RequestBody OrderDTO orderDTO){
        Order order = buildBO(orderDTO);
        PageInfo<Order> orders = orderService.searchProgressOrder(order);
        return ResponseUtil.success(PageUtils.getPageInfo(orders,orders.getList()
                .stream()
                .map(Order::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/searchHistoryOrder")
    public Response<PageInfo<OrderDTO>> searchHistoryOrder(@RequestBody OrderDTO orderDTO){
        Order order = buildBO(orderDTO);
        PageInfo<Order> orders = orderService.searchHistoryOrder(order);
        return ResponseUtil.success(PageUtils.getPageInfo(orders,orders.getList()
                .stream()
                .map(Order::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/searchExceptionOrder")
    public Response<PageInfo<ExceptionOrderDTO>> searchExceptionOrder(@RequestBody OrderDTO orderDTO){
        Order order = buildBO(orderDTO);
        PageInfo<ExceptionOrder> orders = exceptionOrderService.searchExceptionOrder(order);
        return ResponseUtil.success(PageUtils.getPageInfo(orders,orders.getList()
                .stream()
                .map(ExceptionOrder::doBackward)
                .collect(Collectors.toList())));
    }


    private List<Integer> getSelectStatue(OrderInfoRequest request){
        List<Integer> selectStatueIds = new ArrayList<>();
        if(request.getSelectStatue() != null && !request.getSelectStatue().equals("")){
            String selectStatue = request.getSelectStatue();
            selectStatueIds =  Arrays.stream(selectStatue.split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        return selectStatueIds;
    }

    private Order buildOrder(orderSuccessRequest request){
        Order order = new Order();
        order.setOrderId(request.getOrderId());
        return order;
    }

    private Order buildOrder(takeMealRequest request){
        Order order = new Order();
        order.setOrderId(request.getOrderId());
        return order;
    }

    private Order buildOrder(makeOrderRequest request){
        Order order = new Order();
        order.setOrderId(request.getOrderId());
        return order;
    }

    private Order buildOrder(OrderInfoRequest request){
        Order order = new Order();
        order.setUid(request.getUid());
        order.setOrderId(request.getOrderId());
        order.setStoreId(request.getStoreId());
        order.setPageNum(request.getPageNum());
        order.setPageSize(request.getPageSize());
        return order;
    }

    private Order buildOrder(PayRequest request){
        Order order = new Order();
        order.setOrderId(request.getOrderId());
        order.setPayMethod(request.getPayMethod());
        return order;
    }

    private Order buildOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setOrderDetail(request.getOrderDetail());
        order.setStoreId(request.getStoreId());
        order.setUid(request.getUid());
        order.setTakeMethod(request.getTakeMethod());
        return order;
    }

    private Order buildBO(OrderDTO orderDTO) {
        return BOUtils.convert(Order.class, orderDTO);
    }

}
