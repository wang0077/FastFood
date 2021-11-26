package com.wang.productcenter.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: wAnG
 * @Date: 2021/11/26 16:43
 * @Description: 请求拦截器，打印请求和响应日志
 */

@Slf4j
@Component
public class RequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String IP = request.getLocalAddr();
        String remoteName = request.getServerName();
        log.info("[Request In] ==> IP : [{}] | Method : [{}] | URI : [{}] | remoteName : [{}]",IP,method,requestURI,remoteName);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        int status = response.getStatus();
        Object body = request.getSession().getAttribute("body");
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String IP = request.getLocalAddr();
        String remoteName = request.getServerName();
        log.info("[Request Out] ==> IP : [{}] | Method : [{}] | URI : [{}] | remoteName : [{}] | \n " +
                "status : [{}] | body : [{}]",IP,method,requestURI,remoteName,status,body.toString());
    }

}
