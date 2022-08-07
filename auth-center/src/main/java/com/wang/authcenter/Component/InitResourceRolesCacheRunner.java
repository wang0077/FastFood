package com.wang.authcenter.Component;

import com.wang.authcenter.Util.redisUtil;
import com.wang.fastfood.apicommons.constant.AuthConstants;
import com.wang.authcenter.entity.Authorization;
import com.wang.authcenter.service.impl.AuthorizationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/4/4 21:42
 * @Description:
 */

@Component
public class InitResourceRolesCacheRunner implements CommandLineRunner {

    @Autowired
    AuthorizationImpl authorization;

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    @Override
    public void run(String... args) {
        List<Authorization> authorization = this.authorization.getAuthorization();
        List<String> url = authorization.stream().map(Authorization::getUrl).collect(Collectors.toList());
        List<List<String>> roleIds = authorization.stream()
                .map(role -> {
                    String[] ids = role.getRoleId().split(",");
                    List<String> Role = Arrays.asList(ids);
                    Role = Role.stream().map(id -> id = "ROLE_".concat(id)).collect(Collectors.toList());
                    return Role;
                })
                .collect(Collectors.toList());
        redisUtil.hmset(AuthConstants.REDIS_PERMISSION_PATH,url,roleIds);
    }
}
