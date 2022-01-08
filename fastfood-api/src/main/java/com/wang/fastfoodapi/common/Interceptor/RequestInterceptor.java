package com.wang.fastfoodapi.common.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Auther: wAnG
 * @Date: 2021/11/26 16:43
 * @Description:
 */

@Slf4j
@Component
public class RequestInterceptor implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String method = Objects.requireNonNull(request.getMethod()).name();
        String requestURI = request.getURI().getPath();
        String IP = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        String remoteName = request.getRemoteAddress().getHostName();
        log.info("[Request In] ==> IP : [{}] | Method : [{}] | URI : [{}] | remoteName : [{}]",IP,method,requestURI,remoteName);
        return chain.filter(exchange);
    }

}
