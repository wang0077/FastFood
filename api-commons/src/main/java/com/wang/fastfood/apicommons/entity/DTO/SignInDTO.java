package com.wang.fastfood.apicommons.entity.DTO;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 20:48
 * @Description:
 */

@Data
public class SignInDTO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 连续签到次数
     */
    private Integer continuousCount;
    /**
     * 允许签到
     */
    private Boolean allowedCheckIn;
    /**
     * 签到分数
     */
    private Integer checkInPoint;


}
