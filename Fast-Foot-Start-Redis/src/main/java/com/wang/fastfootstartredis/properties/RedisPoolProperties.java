package com.wang.fastfootstartredis.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: wAnG
 * @Date: 2022/1/11 20:17
 * @Description:
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "redis.pool")
public class RedisPoolProperties {

    private int maxActive;

    private int maxIdle;

    private int minIdle;

    private long maxWaitMillis;
}
