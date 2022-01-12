package com.wang.fastfootstartredis.config;

import com.wang.fastfootstartredis.properties.RedisPoolProperties;
import com.wang.fastfootstartredis.properties.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Auther: wAnG
 * @Date: 2021/11/28 16:34
 * @Description:
 */

@Slf4j
@Configuration
@EnableConfigurationProperties({RedisProperties.class, RedisPoolProperties.class})
public class RedisConfig {

    private final RedisProperties redisProperties;

    private final RedisPoolProperties redisPoolProperties;

    public RedisConfig(RedisProperties redisProperties,RedisPoolProperties redisPoolProperties){
        this.redisPoolProperties = redisPoolProperties;
        this.redisProperties = redisProperties;
    }

    @Bean
    JedisPool jedisPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(redisPoolProperties.getMaxIdle());
        config.setMaxWaitMillis(redisPoolProperties.getMaxWaitMillis());
        config.setMinIdle(redisPoolProperties.getMinIdle());
        config.setMaxTotal(redisPoolProperties.getMaxActive());
        config.setBlockWhenExhausted(true);
        JedisPool pool = new JedisPool(config, redisProperties.getHost()
                , redisProperties.getPort()
                , redisProperties.getTimeout()
                , redisProperties.getPassword()
                , redisProperties.getDatabase());
        log.info("[Spring init] ==> redisPool创建成功 | redis地址 : [{}]",redisProperties.getHost() + ":" + redisProperties.getPort());
        return pool;
    }
}
