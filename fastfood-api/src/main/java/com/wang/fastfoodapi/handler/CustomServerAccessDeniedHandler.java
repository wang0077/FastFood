package com.wang.fastfoodapi.handler;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfoodapi.Util.JSONUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @Auther: wAnG
 * @Date: 2022/4/6 20:47
 * @Description:
 */

@Component
public class CustomServerAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        ServerHttpResponse response=exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().set("Access-Control-Allow-Origin","*");
        response.getHeaders().set("Cache-Control","no-cache");
        String body= JSONUtil.toJsonString(ResponseUtil.fail(CodeEnum.USER_ACCESS_UNAUTHORIZED));
        DataBuffer buffer =  response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
