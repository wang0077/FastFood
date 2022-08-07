package com.wang.accountcenter;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/4/23 15:45
 * @Description:
 */

@Data
public class rechargeAmountRequest {
    private String openId;

    private Double amount;
}
