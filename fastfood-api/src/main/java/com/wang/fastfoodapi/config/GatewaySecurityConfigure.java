package com.wang.fastfoodapi.config;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.constant.AuthConstants;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfoodapi.Util.JSONUtil;
import com.wang.fastfoodapi.Util.redisUtil;
import com.wang.fastfoodapi.handler.CustomServerAccessDeniedHandler;
import com.wang.fastfoodapi.handler.CustomServerAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: wAnG
 * @Date: 2022/4/3 22:26
 * @Description:
 */

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfigure {
    @Autowired
    AuthorizationManager authorizationManager;

    @Autowired
    private CustomServerAccessDeniedHandler customServerAccessDeniedHandler;

    @Autowired
    private CustomServerAuthenticationEntryPoint customServerAuthenticationEntryPoint;

    private static final String PERMIT_ROLE = "ROLE_0";

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());

        Map<String, String> permissionPath = redisUtil.hgetAll(AuthConstants.REDIS_PERMISSION_PATH);

        List<String> permitPath = new ArrayList<>();
        for (String JsonPath : permissionPath.keySet()) {
            String pathRole = permissionPath.get(JsonPath);
            List<String> role = JSONUtil.parseToList(pathRole, String.class);
            if(role.contains(PERMIT_ROLE)){
                permitPath.add(JsonPath);
            }
        }
        String[] path = new String[permitPath.size()];
        permitPath.toArray(path);

        //配置白名单和访问规则，CommonEnum枚举类
        http.authorizeExchange()
                .pathMatchers(path).permitAll()
                .anyExchange().access(authorizationManager)
                .and().exceptionHandling()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(customServerAccessDeniedHandler)
                .authenticationEntryPoint(customServerAuthenticationEntryPoint)
                .and()
                .csrf().disable();
        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(AuthConstants.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AuthConstants.AUTHORITY_CLAIM_NAME);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    @Bean
    ServerAuthenticationEntryPoint authenticationEntryPoint(){
        return ((exchange, e) -> {
            Mono<Void> mono = Mono.defer(() -> Mono.just(exchange.getResponse()))
                    .flatMap(response -> {
                        response.setStatusCode(HttpStatus.OK);
                        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                        response.getHeaders().set("Access-Control-ALLow-Origin","*");
                        response.getHeaders().set("Cache-Control","no-cache");
                        String body = JSONUtil.toJsonString(ResponseUtil.fail(CodeEnum.TOKEN_INVALID_OR_EXPIRED));
                        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                        return response.writeWith(Mono.just(buffer))
                                .doOnError(error -> DataBufferUtils.release(buffer));
                    });
            return mono;
        });
    }

}
