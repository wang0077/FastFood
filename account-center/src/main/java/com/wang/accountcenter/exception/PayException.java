package com.wang.accountcenter.exception;

import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.exception.FastFoodException;

/**
 * @Auther: wAnG
 * @Date: 2022/4/23 18:58
 * @Description:
 */

public class PayException extends FastFoodException {
    public PayException(String message) {
        super(message, CodeEnum.PAY_ERROR);
    }
}
