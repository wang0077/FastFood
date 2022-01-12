package com.wang.fastfootstartredis.config;

import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Util.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Auther: wAnG
 * @Date: 2022/1/11 16:03
 * @Description:
 */

@Configuration
@Import(value = {RedisConfig.class, RedisUtil.class})
public class RedisAutoConfig {

    @Bean
    public AsyncRedis redisService(){
        return new AsyncRedis();
    }

}
