package com.wang.authcenter.filter;

import com.wang.authcenter.Util.JSONUtil;
import com.wang.authcenter.Util.JwtUtils;
import com.wang.authcenter.service.impl.UserService;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 19:07
 * @Description:
 */

public class JwtAuthTokenFilter extends OncePerRequestFilter {

    @Value("${token.tokenHeader}")
    private String tokenHeader;

    @Value("${token.tokenHead}")
    private String tokenHead;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(tokenHeader);
        //存在token
        if (null != authHeader && authHeader.startsWith(tokenHead)) {
            String authToken = authHeader.substring(tokenHead.length());
            String username = JwtUtils.getUserNameFromToken(authToken);
            //token存在用户名但未登录
            if (null != username && null == SecurityContextHolder.getContext().getAuthentication()) {
                //登录
                UserDetails userDetails = userService.loadUserByUsername(username);
                //验证token是否有效，重新设置用户对象
                try {
                    if (JwtUtils.validateToken(authToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    response.setContentType("application/json;charset=utf-8");
                    Response<String> result = ResponseUtil.fail(403, "凭证已失效，请重新登录！");
                    PrintWriter out = response.getWriter();
                    out.write(JSONUtil.toJsonString(result));
                    out.flush();
                    out.close();
                    return;
                }
            }

        }
        filterChain.doFilter(request, response);
    }
}
