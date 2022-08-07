package com.wang.tradecenter.Interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: wAnG
 * @Date: 2021/12/8 21:34
 * @Description:
 */

@Component
public class CrossInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        if (request.getHeader(HttpHeaders.ORIGIN) != null) {
            String origin = request.getHeader("Origin");
            response.addHeader("Access-Control-Allow-Origin", origin);
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT,PATCH, HEAD");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            response.addHeader("Access-Control-Max-Age", "3600");
        }
        return true;
    }
}
