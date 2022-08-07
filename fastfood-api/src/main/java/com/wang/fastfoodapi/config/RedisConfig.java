package com.wang.fastfoodapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Auther: wAnG
 * @Date: 2022/4/5 01:17
 * @Description:
 */

@Configuration
@PropertySource("classpath:application.properties")
public class RedisConfig {
    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.timeout}")
    private int timeout;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.pool.max-idle}")
    private int maxIdle;

    @Value("${redis.pool.max-wait-millis}")
    private int maxWaitMillis;

//    @Value("${redis.pool.blockWhenExhausted}")
//    private Boolean blockWhenExhausted;


    @Bean
    public JedisPool jedisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        // 连接耗尽时是否阻塞, false报异常,true阻塞直到超时, 默认true
//        jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout,password);
        return jedisPool;
    }
}
