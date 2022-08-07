package com.wang.fastfoodapi.config;

import com.wang.fastfood.apicommons.constant.AuthConstants;
import com.wang.fastfoodapi.Util.JSONUtil;
import com.wang.fastfoodapi.Util.redisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: wAnG
 * @Date: 2022/4/4 15:58
 * @Description:
 */

@Component
@Slf4j
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        String path = request.getURI().getPath();
        PathMatcher pathMatcher = new AntPathMatcher();

        // 1. 对应跨域的预检请求直接放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }

        // 2. token为空拒绝访问
        log.debug("????>>>>");
        String token = request.getHeaders().getFirst(AuthConstants.JWT_TOKEN_HEADER);
        if (StringUtils.isBlank(token)) {
            return Mono.just(new AuthorizationDecision(false));
        }
        // 2、认证通过且角色匹配的用户可访问当前路径
        List<String> authorities = new ArrayList<>();


        Map<String, String> permissionPath = redisUtil.hgetAll(AuthConstants.REDIS_PERMISSION_PATH);

        for (String JsonPath : permissionPath.keySet()) {
            if (pathMatcher.match(JsonPath, path)) {
                authorities.addAll(JSONUtil.parseToList(permissionPath.get(JsonPath), String.class));
            }
        }

        Mono<AuthorizationDecision> authorizationDecisionMono = authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(roleId -> {
                    // 5. roleId是请求用户的角色(格式:ROLE_{roleId})，authorities是请求资源所需要角色的集合
                    log.info("访问路径：{}", path);
                    log.info("用户角色roleId：{}", roleId);
                    log.info("资源需要权限authorities：{}", authorities);
                    return authorities.contains(roleId);
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));

        return authorizationDecisionMono;
//        return authentication
//                .filter(Authentication::isAuthenticated)
//                .flatMapIterable(Authentication::getAuthorities)
//                .map(GrantedAuthority::getAuthority)
//                .any(id -> authorities.contains("1"))
//                .map(AuthorizationDecision::new)
//                .defaultIfEmpty(new AuthorizationDecision(true));
    }
}
