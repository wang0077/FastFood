package com.wang.fastfood.apicommons.entity.response;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/4/26 17:55
 * @Description:
 */

@Data
public class UserInfoResponse {

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
     * 用户权限
     */
    private Integer role;
    /**
     * 性别
     */
    private Integer sex;


}
