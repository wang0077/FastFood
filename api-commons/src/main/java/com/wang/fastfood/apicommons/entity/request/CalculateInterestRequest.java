package com.wang.fastfood.apicommons.entity.request;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/4/24 19:23
 * @Description:
 */

@Data
public class CalculateInterestRequest {

    private Double amount;

    private String uid;

}
