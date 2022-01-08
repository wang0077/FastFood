package com.wang.storeCenter.entity.RocketMQ;

import com.wang.storeCenter.enums.RedisOption;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: wAnG
 * @Date: 2021/12/19 19:28
 * @Description:
 */

@Data
public class RedisMessage implements Serializable {

    private String key;

    private Object payload;

    private int indexDB;

    private int seconds;

    private String[] delKeys;

    private RedisOption option;

}
