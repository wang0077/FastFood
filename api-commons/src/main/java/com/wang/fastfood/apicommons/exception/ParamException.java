package com.wang.fastfood.apicommons.exception;

/**
 * @Auther: wAnG
 * @Date: 2021/11/25 20:42
 * @Description:
 */

public class ParamException extends RuntimeException {
    public ParamException(String msg){
        super(msg);
    }
}
