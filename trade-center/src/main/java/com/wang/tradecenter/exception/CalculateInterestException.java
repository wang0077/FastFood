package com.wang.tradecenter.exception;

import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.exception.FastFoodException;

/**
 * @Auther: wAnG
 * @Date: 2022/4/24 21:25
 * @Description:
 */

public class CalculateInterestException extends FastFoodException {
    public CalculateInterestException(String message) {
        super(message, CodeEnum.SERVER_ERROR);
    }
}
