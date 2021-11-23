package com.wang.fastfood.apicommons.entity.BO;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Auther: wAnG
 * @Date: 2021/11/23 22:06
 * @Description:
 */

@Data
@Accessors(chain = true)
public class User {
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
