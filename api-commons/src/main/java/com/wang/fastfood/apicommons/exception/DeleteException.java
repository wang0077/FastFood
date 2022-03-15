package com.wang.fastfood.apicommons.exception;

import com.wang.fastfood.apicommons.enums.CodeEnum;

/**
 * @Auther: wAnG
 * @Date: 2022/3/15 20:23
 * @Description:
 */

public class DeleteException extends FastFoodException {

    public DeleteException(String message, CodeEnum codeEnum) {
        super(message, codeEnum);
    }
}
