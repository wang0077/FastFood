package com.wang.fastfootstartredis.RocketMQ;

import com.wang.fastfootstartredis.enums.RedisOption;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/19 19:28
 * @Description:
 */

@Data
public class RedisMessage implements Serializable {

    private String key;

    private Object payload;

    private List<Object> payloads;

    private int indexDB;

    private int seconds;

    private String[] delKeys;

    private RedisOption option;

    private String memberNum;

    private String field;

    private List<String> fields;

}
