package com.wang.fastfood.apicommons.entity.request;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/4/23 18:49
 * @Description:
 */

@Data
public class PayRequest {

    private String uid;

    private Double amount;
}
