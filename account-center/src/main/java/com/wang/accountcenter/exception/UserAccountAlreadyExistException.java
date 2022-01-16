package com.wang.accountcenter.exception;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 16:16
 * @Description:
 */

public class UserAccountAlreadyExistException extends RuntimeException{
    public UserAccountAlreadyExistException(String msg){
        super(msg);
    }
}
