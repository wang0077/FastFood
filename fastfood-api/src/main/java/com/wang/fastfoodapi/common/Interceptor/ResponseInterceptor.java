package com.wang.fastfoodapi.common.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Auther: wAnG
 * @Date: 2022/1/8 01:19
 * @Description:
 */

@Component
@Slf4j
@Order
public class ResponseInterceptor implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        int status = Objects.requireNonNull(response.getStatusCode()).value();
        Object body = request.getBody();
        String method = Objects.requireNonNull(request.getMethod()).name();
        String requestURI = request.getURI().getPath();
        String IP = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        String remoteName = request.getRemoteAddress().getHostName();
        log.info("[Request Out] ==> IP : [{}] | Method : [{}] | URI : [{}] | remoteName : [{}] | \n " +
                "status : [{}] | body : [{}]",IP,method,requestURI,remoteName,status,body.toString());
        return chain.filter(exchange);
    }
}
