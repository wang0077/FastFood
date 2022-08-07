package com.wang.fastfoodapi.enums;

/**
 * @Auther: wAnG
 * @Date: 2021/11/28 17:31
 * @Description:
 */
public enum RedisOption {

    KEYS("keys"),
    PIPEKEYS("pipe-Keys"),
    RADIUS("radius"),
    ADDGEO("addGEO"),
    DELGEO("delGEO"),
    MSET("mset"),
    GET("get"),
    SET("set"),
    SETEX("setex"),
    INCR("incr"),
    INCRBY("incrBy"),
    DECR("decr"),
    DECRBY("decrBy"),
    EXPIRE("expire"),
    ZCARD("zcard"),
    ZADD("zadd"),
    ZREM("zrem"),
    ZRANGE("zrange"),
    HSET("hset"),
    HMSET("hmset"),
    HDEL("hdel"),
    HMGET("hmget"),
    DEL("del");

    private final String opName;

    RedisOption(String opName) {
        this.opName = opName;
    }

    public String getOpName() {
        return opName;
    }
}
