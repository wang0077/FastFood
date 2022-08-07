package com.wang.fastfood.usercenter.exception;

import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.exception.FastFoodException;

/**
 * @Auther: wAnG
 * @Date: 2022/4/24 16:41
 * @Description:
 */

public class PasswordException extends FastFoodException {
    public PasswordException(String message) {
        super(message, CodeEnum.PASSWORD_ERROR);
    }
}
