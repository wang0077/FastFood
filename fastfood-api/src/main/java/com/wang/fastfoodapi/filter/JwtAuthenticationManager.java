package com.wang.fastfoodapi.filter;

import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Auther: wAnG
 * @Date: 2022/4/3 22:18
 * @Description:
 */

//@Component
@Slf4j
public class JwtAuthenticationManager implements GlobalFilter {

    @Autowired
    TokenStore redisTokenStore;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StringUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }
        //从token中解析用户信息并设置到Header中去
        String realToken = token.replace("Bearer ", "");


        return chain.filter(exchange);
    }
}
