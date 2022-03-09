//package com.wang.fastfoodapi.filter;
//
//import com.wang.fastfoodapi.Util.JwtUtils;
//import com.wang.fastfoodapi.service.impl.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.util.StringUtils;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
///**
// * @Auther: wAnG
// * @Date: 2022/1/4 19:07
// * @Description:
// */
//
//public class JwtAuthTokenFilter implements WebFilter {
//
//    @Value("${token.tokenHeader}")
//    private String tokenHeader;
//
//    @Value("${token.tokenHead}")
//    private String tokenHead;
//
//    @Autowired
//    private UserService userService;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        HttpHeaders headers = exchange.getRequest().getHeaders();
//        ServerHttpRequest request = exchange.getRequest();
//        String authHeader = headers.getFirst(tokenHeader);
//        if(authHeader != null && authHeader.startsWith(tokenHead)){
//            String authToken = authHeader.substring(tokenHead.length());
//            String username = JwtUtils.getUserNameFromToken(authToken);
//            Authentication authentication = JwtUtils.getAuthentication(authToken);
//            return chain.filter(exchange)
//                    .subscriberContext(ReactiveSecurityContextHolder.withAuthentication(authentication));
//        }
//        return chain.filter(exchange);
//    }
//
//    private String resolveToken(ServerHttpRequest request) {
//        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenHead)) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}
