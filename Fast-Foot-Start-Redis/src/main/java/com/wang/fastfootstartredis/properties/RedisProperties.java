package com.wang.fastfootstartredis.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: wAnG
 * @Date: 2022/1/11 17:33
 * @Description:
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private String host;

    private int port;

    private int timeout;

    private String password;

    private int database;

}
