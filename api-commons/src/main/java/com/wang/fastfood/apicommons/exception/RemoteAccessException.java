package com.wang.fastfood.apicommons.exception;

/**
 * @Auther: wAnG
 * @Date: 2022/4/10 02:46
 * @Description:
 */

public class RemoteAccessException extends RuntimeException{
    public RemoteAccessException(){
        super("远程调用异常");
    }
}
