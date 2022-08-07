package com.wang.fastfood.usercenter.exception;

import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.exception.FastFoodException;

/**
 * @Auther: wAnG
 * @Date: 2022/4/24 14:34
 * @Description:
 */

public class RegisterParamMiss extends FastFoodException {

    public RegisterParamMiss(String message) {
        super(message, CodeEnum.PARAM_MISS);
    }
}
