package com.wang.fastfood.apicommons.entity.DTO;

import com.wang.fastfood.apicommons.entity.common.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/**
 * @Auther: wAnG
 * @Date: 2022/1/2 20:24
 * @Description:
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO extends BaseRequest implements UserDetails {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 电话号码
     */
    private String phoneNumber;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 用户入会时间
     */
    private Date joinTime;
    /**
     * 用户等级
     */
    private Integer userLevel;
    /**
     * 用户经验值
     */
    private Integer experience;

    @Override
    public void validity() {

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
