package com.wang.authcenter.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.wang.authcenter.security.extension.WechatAuthenticationProvider;
import com.wang.authcenter.service.Remote.UserRemote;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Auther: wAnG
 * @Date: 2022/4/3 17:15
 * @Description:
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    private final WxMaService wxMaService;

    private UserRemote userRemote;
    

    /**
     * 注入AuthenticationManager接口，启用OAuth2密码模式
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public WechatAuthenticationProvider wechatAuthenticationProvider() {
        WechatAuthenticationProvider provider = new WechatAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setWxMaService(wxMaService);
        provider.setUserRemote(userRemote);
        return provider;
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(wechatAuthenticationProvider());
    }

    /**
     * 通过HttpSecurity实现Security的自定义过滤配置
     *
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .requestMatchers().anyRequest()
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**").permitAll();
    }



    @Bean
    public PasswordEncoder passwordEncoder()  {
        return new BCryptPasswordEncoder();
    }

}
