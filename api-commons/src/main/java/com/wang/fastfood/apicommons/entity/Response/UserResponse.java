package com.wang.fastfood.apicommons.entity.Response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @Auther: wAnG
 * @Date: 2021/11/23 23:47
 * @Description:
 */

@Data
public class UserResponse {

    private static final long serialVersionUID=1L;

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
    private String userName;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 性别
     */
    private Boolean sex;

    /**
     * 生日
     */
    private LocalDateTime birthday;

    /**
     * 用户入会时间
     */
    private LocalDateTime joinTime;

    /**
     * 用户等级
     */
    private Integer userLevel;

    /**
     * 用户经验值
     */
    private Integer experience;

}
