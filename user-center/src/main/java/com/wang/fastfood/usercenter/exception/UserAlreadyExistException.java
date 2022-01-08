package com.wang.fastfood.usercenter.exception;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 16:16
 * @Description:
 */

public class UserAlreadyExistException extends Exception{
    public UserAlreadyExistException(String msg){
        super(msg);
    }
}
