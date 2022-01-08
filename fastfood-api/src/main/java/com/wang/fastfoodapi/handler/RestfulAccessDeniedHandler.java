package com.wang.fastfoodapi.handler;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfoodapi.Util.JSONUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 19:15
 * @Description:
 */

@Component
public class RestfulAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().set(HttpHeaders.ACCEPT_CHARSET,"UST-8");
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE,"application/jso");
        String body = JSONUtil.toJsonString(ResponseUtil.fail(403, "权限不足，请联系管理员！"));
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

//    @Override
//    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json");
//        PrintWriter out = response.getWriter();
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", 403);
//        map.put("message", "权限不足，请联系管理员！");
//        out.write(new ObjectMapper().writeValueAsString(map));
//        out.flush();
//        out.close();
//    }
}
