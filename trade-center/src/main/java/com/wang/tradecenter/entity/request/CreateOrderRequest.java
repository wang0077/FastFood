package com.wang.tradecenter.entity.request;

import com.wang.fastfood.apicommons.entity.DTO.OrderDetail;
import lombok.Data;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/22 19:32
 * @Description:
 */

@Data
public class CreateOrderRequest {

    /**
     * 门店ID
     */
    private String storeId;
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 取餐方式
     */
    private Integer takeMethod;
    /**
     * 订单商品详情
     */
    private List<OrderDetail> orderDetail;

}
