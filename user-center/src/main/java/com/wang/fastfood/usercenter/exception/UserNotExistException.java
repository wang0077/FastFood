package com.wang.fastfood.usercenter.exception;

/**
 * @Auther: wAnG
 * @Date: 2022/4/8 17:13
 * @Description:
 */

public class UserNotExistException extends RuntimeException{

    public UserNotExistException(String meg){
        super(meg);
    }

}
