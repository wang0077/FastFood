package com.wang.tradecenter.entity.response;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/3/22 19:13
 * @Description:
 */

@Data
public class CreateOrderResponse {

    private String orderId;

    private double amount;

}
