package com.wang.tradecenter.exception;

/**
 * @Auther: wAnG
 * @Date: 2022/3/31 19:12
 * @Description:
 */

public class OrderNotFindException extends RuntimeException{

    public OrderNotFindException(String message){
        super(message);
    }

}
