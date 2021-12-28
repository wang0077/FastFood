package com.wang.productcenter.Redis;

import com.wang.productcenter.Util.JSONUtil;
import com.wang.productcenter.Util.RedisUtil;
import com.wang.productcenter.entity.RocketMQ.RedisMessage;
import com.wang.productcenter.enums.RedisOption;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/18 21:54
 * @Description:
 */

@Service
public class RedisService implements Serializable {

    @Autowired
    private RocketMQTemplate mqTemplate;

    private final int retryCount = 3;

    public String setex(String key, Object body, int seconds) {
        return setex(key, body, seconds, 0);
    }

    public String setex(String key, Object body, int seconds, int indexDB) {
        String result = null;
        int count = 0;
        while (count < retryCount && !"OK".equals(result)) {
            count++;
            result = RedisUtil.set(key, body);
        }
        if (count >= retryCount && !"OK".equals(result)) {
            RedisMessage message = buildMessage(key, body, indexDB, seconds, RedisOption.SETEX);
            sendMessage(message);
            result = "MQ-Retry";
        }
        return result;
    }

    public String set(String key, Object body) {
        return set(key, body, 0);
    }

    public String set(String key, Object body, int indexDB) {
        String result = null;
        int count = 0;
        while (count < retryCount && !"OK".equals(result)) {
            count++;
            result = RedisUtil.set(key, body);
        }
        if (count >= retryCount && !"OK".equals(result)) {
            RedisMessage message = buildMessage(key, body, indexDB, RedisOption.SET);
            sendMessage(message);
            result = "MQ-Retry";
        }
        return result;
    }

    public String del(List<String> keys){
        return del(keys.toArray(new String[0]));
    }

    public String del(String... keys) {
        return del(0, keys);
    }

    public String del(int indexDB, String... keys) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.del(indexDB, keys);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(indexDB, RedisOption.DEL, keys);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String incr(String key) {
        return incr(key, 0);
    }

    public String incr(String key, int indexDB) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.incr(key, indexDB);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(key, indexDB, RedisOption.INCR);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String incrBy(String key, Long value, int indexDB) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.incrBy(key, value, indexDB);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(key, value, indexDB, RedisOption.INCRBY);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String decr(String key) {
        return decr(key, 0);
    }

    public String decr(String key, int indexDB) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.decr(key, indexDB);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(key, indexDB, RedisOption.DECR);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String decrBy(String key,Long value){
        return decrBy(key,value,0);
    }

    public String decrBy(String key, Long value, int indexDB) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.decrBy(key, value, indexDB);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(key,value, indexDB, RedisOption.DECRBY);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String expire(String key,int value,int indexDB){
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.expire(key, value, indexDB);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(key,value, indexDB, RedisOption.EXPIRE);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    private void sendMessage(RedisMessage message) {
        String json = JSONUtil.toJsonString(message);
        mqTemplate.convertAndSend("RedisOption", json);
    }

    private RedisMessage buildMessage(String key, int indexDB, RedisOption option) {
        RedisMessage mq = new RedisMessage();
        mq.setKey(key);
        mq.setOption(option);
        mq.setIndexDB(indexDB);
        return mq;
    }

    private RedisMessage buildMessage(int indexDB, RedisOption option, String... keys) {
        RedisMessage mq = new RedisMessage();
        mq.setDelKeys(keys);
        mq.setOption(option);
        mq.setIndexDB(indexDB);
        return mq;
    }

    private RedisMessage buildMessage(String key, Object body, int indexDB, RedisOption option) {
        RedisMessage mq = new RedisMessage();
        mq.setKey(key);
        mq.setPayload(body);
        mq.setOption(option);
        mq.setIndexDB(indexDB);
        return mq;
    }

    private RedisMessage buildMessage(String key, Object body, int seconds, int indexDB, RedisOption option) {
        RedisMessage mq = new RedisMessage();
        mq.setKey(key);
        mq.setPayload(body);
        mq.setSeconds(seconds);
        mq.setOption(option);
        mq.setIndexDB(indexDB);
        return mq;
    }
}
