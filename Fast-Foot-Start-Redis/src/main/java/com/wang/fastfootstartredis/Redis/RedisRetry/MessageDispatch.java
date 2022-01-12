package com.wang.fastfootstartredis.Redis.RedisRetry;

import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Redis.RedisRetry.Functional.RedisRetryFunctional;
import com.wang.fastfootstartredis.RocketMQ.RedisMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: wAnG
 * @Date: 2021/12/20 02:33
 * @Description:
 */

@Component
public class MessageDispatch implements InitializingBean {

    // todo RedisMessage后期可能更改if的方法

    private final AsyncRedis asyncRedis;

    private final Map<String, RedisRetryFunctional<String, Object>> retryFunctionalMap = new HashMap<>();

    public MessageDispatch(AsyncRedis asyncRedis){
        this.asyncRedis = asyncRedis;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        retryFunctionalMap.put("get",(asyncRedis::set));
    }

    public RedisRetryFunctional<String,Object> dispatch(RedisMessage redisMessage) {
       return retryFunctionalMap.get(redisMessage.getKey());
    }
}
