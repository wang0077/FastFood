package com.wang.productcenter.RedisRetry;

import com.wang.productcenter.RedisRetry.Functional.RedisRetryFunctional;
import com.wang.productcenter.entity.RocketMQ.RedisMessage;
import com.wang.productcenter.service.impl.RedisService;
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
