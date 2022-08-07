package com.wang.tradecenter.entity.request;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/3/25 02:50
 * @Description:
 */

@Data
public class PayRequest {

    private String orderId;

    /**
     * 支付方式
     */
    private Integer payMethod;

}
