package com.wang.storeCenter.Redis.RedisRetry;

import com.wang.storeCenter.Redis.RedisRetry.Functional.RedisRetryFunctional;
import com.wang.storeCenter.Redis.RedisService;
import com.wang.storeCenter.entity.RocketMQ.RedisMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: wAnG
 * @Date: 2021/12/20 02:33
 * @Description:
 */

@Service
public class MessageDispatch implements InitializingBean {

    // todo RedisMessage后期可能更改if的方法

    @Autowired
    private RedisService redisService;

    private final Map<String, RedisRetryFunctional<String, Object>> retryFunctionalMap = new HashMap<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        retryFunctionalMap.put("get",((key, body) -> redisService.set(key,body)));
    }

    public RedisRetryFunctional<String,Object> dispatch(RedisMessage redisMessage) {
       return retryFunctionalMap.get(redisMessage.getKey());
    }
}
