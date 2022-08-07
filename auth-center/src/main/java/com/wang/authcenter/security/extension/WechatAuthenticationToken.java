package com.wang.authcenter.security.extension;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * @Auther: wAnG
 * @Date: 2022/4/7 17:34
 * @Description:
 */

public class WechatAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 550L;

    private final Object principal;
    @Getter
    private String encryptedData;
    @Getter
    private String iv;

    /**
     * 账号校验之前的token构建
     *
     */
    public WechatAuthenticationToken(Object principal, String encryptedData,String iv) {
        super(null);
        this.principal = principal;
        this.encryptedData = encryptedData;
        this.iv=iv;
        setAuthenticated(false);
    }

    /**
     * 账号校验成功之后的token构建
     *
     */
    public WechatAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
