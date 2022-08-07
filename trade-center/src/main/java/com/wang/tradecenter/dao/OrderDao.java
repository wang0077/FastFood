package com.wang.tradecenter.dao;

import com.wang.tradecenter.entity.PO.OrderPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/20 17:43
 * @Description:
 */


@Mapper
public interface OrderDao {

    int createOrder(@Param("order")OrderPO order);

    OrderPO getOrderByOrderId(@Param("orderId") String orderId);

    int updateByOrderId(@Param("order")OrderPO order);

    /**
     *  获取状态是正在进行的订单信息
     */
    List<OrderPO> getOrderProgressByStoreId(@Param("order") OrderPO order,@Param("selectStatue") List<Integer> selectStatue);

    List<OrderPO> getTakeStatueOrderByStoreId(@Param("order") OrderPO order);

    List<OrderPO> getHistoryOrderInfoByUid(@Param("uid") String openId);

    int changeStatue(@Param("orderId")String orderId, @Param("statue")int statue);

    int changeStatueAndTakeNumber(@Param("orderId")String orderId, @Param("statue")int statue,@Param("number")Integer takeNumber);

    int changeStatueAndCompleteTime(@Param("orderId")String orderId, @Param("statue")int statue,@Param("time") LocalDateTime time);

    int addOrderPayMethod(@Param("orderId")String orderId,@Param("payMethod")Integer payMethod);

    List<OrderPO> getByOrderIds(@Param("orderIds") List<String> orderIds);

    List<OrderPO> getStoreHistoryOderInfo(@Param("order") OrderPO order);

    List<OrderPO> searchProgressOrder(@Param("order") OrderPO order);

    List<OrderPO> searchHistoryOrder(@Param("order")OrderPO order);

    List<OrderPO> searchExceptionOrder(@Param("order")OrderPO order);
}
