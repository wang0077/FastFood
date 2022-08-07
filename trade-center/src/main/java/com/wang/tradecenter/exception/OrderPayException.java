package com.wang.tradecenter.exception;

import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.exception.FastFoodException;

/**
 * @Auther: wAnG
 * @Date: 2022/4/23 19:08
 * @Description:
 */


public class OrderPayException extends FastFoodException {
    public OrderPayException(String message) {
        super(message, CodeEnum.PAY_ERROR);
    }
}
