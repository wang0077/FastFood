package com.wang.authcenter.config;

import com.wang.authcenter.security.extension.WechatTokenGranter;
import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.*;

/**
 * @Auther: wAnG
 * @Date: 2022/4/3 16:42
 * @Description:
 */

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final String RESOURCE_IDS = "order";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        String finalSecret = new BCryptPasswordEncoder().encode("123456");
        clients.inMemory()
                .withClient("client_2")
                .resourceIds(RESOURCE_IDS)
                .authorizedGrantTypes("password","refresh_token","wechat")
                .scopes("all")
                .authorities("oauth2")
                .secret(finalSecret);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancers.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        // 获取原有默认授权模式(授权码模式、密码模式、客户端模式、简化模式)的授权者
        List<TokenGranter> granterList = new ArrayList<>(Arrays.asList(endpoints.getTokenGranter()));

        // 添加微信授权模式的授权者
        granterList.add(new WechatTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(), authenticationManager
        ));

        CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(granterList);
        endpoints.authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter())
                .tokenEnhancer(tokenEnhancerChain)
                .userDetailsService(userDetailsService)
                .tokenGranter(compositeTokenGranter)
                // refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
                //      1.重复使用：access_token过期刷新时， refresh token过期时间未改变，仍以初次生成的时间为准
                //      2.非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新而无需失效再次登录
                .reuseRefreshTokens(false);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        return converter;
    }

    /**
     * 从classpath下的密钥库中获取密钥对(公钥+私钥)
     */
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(
                new ClassPathResource("FastFood.jks"), "123456".toCharArray());
        KeyPair keyPair = factory.getKeyPair(
                "FastFood", "123456".toCharArray());
        return keyPair;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> map = new HashMap<>(2);
            UserDTO user = (UserDTO) authentication.getUserAuthentication().getPrincipal();
            map.put("UserId", user.getUid());
            map.put("userName",user.getUsername());
            map.put("authorization",user.getRole().toString());
            if(user.getRole() == 2){
                map.put("StoreId",user.getStoreId());
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
            return accessToken;
        };
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .allowFormAuthenticationForClients();
        super.configure(security);
    }
}
