package com.wang.fastfoodapi.handler;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfoodapi.Util.JSONUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 19:24
 * @Description:
 */

@Component
public class RestAuthorizationEntryPoint implements ServerAuthenticationEntryPoint {

//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json");
//        PrintWriter out = response.getWriter();
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", 401);
//        map.put("message", "尚未登录，请登录！");
//        out.write(new ObjectMapper().writeValueAsString(map));
//        out.flush();
//        out.close();
//    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.ACCEPT_CHARSET, "UST-8");
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/jso");
        String body = JSONUtil.toJsonString(ResponseUtil.fail(403, "尚未登录，请登录！"));
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
