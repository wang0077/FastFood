package com.wang.fastfood.apicommons.exception;

import com.wang.fastfood.apicommons.enums.CodeEnum;

/**
 * @Auther: wAnG
 * @Date: 2022/3/15 20:26
 * @Description:
 */

public class FastFoodException extends RuntimeException{

    public final CodeEnum codeEnum;

    public FastFoodException(String message, CodeEnum codeEnum) {
        super(message);
        this.codeEnum = codeEnum;
    }


}
