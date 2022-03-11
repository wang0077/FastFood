package com.wang.productcenter.Listener;

import com.wang.productcenter.Redis.RedisService;
import com.wang.productcenter.Util.JSONUtil;
import com.wang.productcenter.entity.RocketMQ.RedisMessage;
import com.wang.productcenter.enums.RedisOption;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auther: wAnG
 * @Date: 2021/12/19 20:53
 * @Description:
 */

//@Component
@RocketMQMessageListener(topic = "RedisOption", consumerGroup = "Product-Center-Group")
public class RedisOptionListener implements RocketMQListener<String> {

    @Autowired
    private RedisService redisService;

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
                result = redisService.set(key, payload, indexDB);
                break;
            case "setex":
                result = redisService.setex(key, payload, seconds, indexDB);
                break;
            case "del":
                result = redisService.del(indexDB, delKeys);
                break;
            case "incr":
                result = redisService.incr(key, indexDB);
                break;
            case "incrBy":
                result = redisService.incrBy(key, (Long) payload, indexDB);
                break;
            case "decr":
                result = redisService.decr(key, indexDB);
                break;
            case "decrBy":
                result = redisService.decrBy(key, (Long) payload, indexDB);
                break;
            case "expire":
                result = redisService.expire(key, (Integer) payload, indexDB);
                break;
        }
    }
}
