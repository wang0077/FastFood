package com.wang.productcenter.enums;

/**
 * @Auther: wAnG
 * @Date: 2021/11/28 17:31
 * @Description:
 */
public enum RedisOption {

    GET("get"),
    SET("set"),
    SETEX("setex"),
    INCR("incr"),
    INCRBY("incrBy"),
    DECR("decr"),
    DECRBY("decrBy"),
    EXPIRE("expire"),
    DEL("del");

    private final String opName;

    RedisOption(String opName) {
        this.opName = opName;
    }

    public String getOpName() {
        return opName;
    }
}
