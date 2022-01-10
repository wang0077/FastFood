package com.wang.authcenter.filter;

import com.wang.authcenter.Util.JSONUtil;
import com.wang.authcenter.Util.JwtUtils;
import com.wang.authcenter.exception.BadRequestException;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: wAnG
 * @Date: 2022/1/5 16:09
 * @Description:
 */

public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

    ThreadLocal<String> currentUsername = new ThreadLocal<>();

    public JwtLoginFilter(String defaultFilterProcessesUrl,AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(authenticationManager);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String jwt = JwtUtils.generateToken(authResult.getName(), authResult.getAuthorities());
        response.setContentType("application/json;charset=utf-8");
        UserDTO user = (UserDTO) authResult.getPrincipal();
        user.setPassword(null);
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("token", jwt);
        Response<Map<String, Object>> result = ResponseUtil.success(CodeEnum.LOGIN_SUCCESS, map);
        PrintWriter out = response.getWriter();
        out.write(JSONUtil.toJsonString(result));
        out.flush();
        out.close();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        String msg = exception.getMessage();
        //登录不成功时，会抛出对应的异常
        if (exception instanceof LockedException) {
            msg = "账号被锁定";
        } else if (exception instanceof CredentialsExpiredException) {
            msg = "密码过期";
        } else if (exception instanceof AccountExpiredException) {
            msg = "账号过期";
        } else if (exception instanceof DisabledException) {
            msg = "账号被禁用";
        } else if (exception instanceof BadCredentialsException) {
            msg = "用户名或密码错误";
        }
        PrintWriter out = response.getWriter();
        out.write(JSONUtil.toJsonString(ResponseUtil.fail(401, msg)));
        out.flush();
        out.close();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            if (!"POST".equals(request.getMethod())) {
                throw new BadRequestException("请求方法错误");
            }
            UserDTO user = JSONUtil.parse(request.getInputStream(), UserDTO.class);
            currentUsername.set(user.getUsername());
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        }catch (BadRequestException exception) {
            response.setContentType("application/json;charset=utf-8");
            Response<String> result = ResponseUtil.fail(CodeEnum.ILLEGAL_REQUEST);
            PrintWriter out = response.getWriter();
            out.write(JSONUtil.toJsonString(result));
            out.flush();
            out.close();
        }
        return null;
    }
}
