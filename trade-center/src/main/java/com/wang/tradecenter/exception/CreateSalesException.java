package com.wang.tradecenter.exception;

import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.exception.FastFoodException;

/**
 * @Auther: wAnG
 * @Date: 2022/4/27 16:30
 * @Description:
 */

public class CreateSalesException extends FastFoodException {
    public CreateSalesException(String message) {
        super(message, CodeEnum.SQL_INSERT_ERROR);
    }
}
