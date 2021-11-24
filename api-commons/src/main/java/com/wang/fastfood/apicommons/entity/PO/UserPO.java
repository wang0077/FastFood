package com.wang.fastfood.apicommons.entity.PO;

import lombok.*;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 *
 * @author wAnG
 * @Date  2021-11-23
 * @Description:  用户表（用户基础信息）
 */

@Data
public class UserPO implements Serializable {

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
     * 验证码
     */
    private String verifyCode;

    /**
     * 用户经验值
     */
    private Integer experience;

    private LocalDateTime crateTime;

    private LocalDateTime updateTime;

}
