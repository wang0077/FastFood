package com.wang.fastfootstartredis.Listener;

import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.RocketMQ.RedisMessage;
import com.wang.fastfootstartredis.Util.JSONUtil;
import com.wang.fastfootstartredis.enums.RedisOption;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Auther: wAnG
 * @Date: 2021/12/19 20:53
 * @Description:
 */

@Component
@RocketMQMessageListener(topic = "RedisOption", consumerGroup = "Product-Center-Group")
public class RedisOptionListener implements RocketMQListener<String> {

    private final AsyncRedis asyncRedis;

    public RedisOptionListener(AsyncRedis asyncRedis){
        this.asyncRedis = asyncRedis;
    }

    @Override
    public void onMessage(String json) {
        RedisMessage message = JSONUtil.parse(json, RedisMessage.class);
        String key = message.getKey();
        Object payload = message.getPayload();
        int indexDB = message.getIndexDB();
        int seconds = message.getSeconds();
        String[] delKeys = message.getDelKeys();
        RedisOption option = message.getOption();
        String result = null;

        //todo : 后期肯定要改掉这恶心的判断
        switch (option.getOpName()) {
            case "set":
                result = asyncRedis.set(key, payload, indexDB);
                break;
            case "setex":
                result = asyncRedis.setex(key, payload, seconds, indexDB);
                break;
            case "del":
                result = asyncRedis.del(indexDB, delKeys);
                break;
            case "incr":
                result = asyncRedis.incr(key, indexDB);
                break;
            case "incrBy":
                result = asyncRedis.incrBy(key, (Long) payload, indexDB);
                break;
            case "decr":
                result = asyncRedis.decr(key, indexDB);
                break;
            case "decrBy":
                result = asyncRedis.decrBy(key, (Long) payload, indexDB);
                break;
            case "expire":
                result = asyncRedis.expire(key, (Integer) payload, indexDB);
                break;
        }
    }
}
