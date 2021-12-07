package com.wang.fastfood.apicommons.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: wAnG
 * @Date: 2021/12/8 00:25
 * @Description:
 */

@Component
@ConfigurationProperties(prefix = "project.mysql")
public class MysqlConfig implements CommonConfig{

    private int PageSize;

}
