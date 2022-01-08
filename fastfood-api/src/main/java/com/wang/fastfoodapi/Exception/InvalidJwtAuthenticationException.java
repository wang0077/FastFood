package com.wang.fastfoodapi.Exception;

/**
 * @Auther: wAnG
 * @Date: 2022/1/7 21:46
 * @Description:
 */

public class InvalidJwtAuthenticationException extends RuntimeException{

    public InvalidJwtAuthenticationException(String msg){
        super(msg);
    }

}
