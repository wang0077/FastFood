package com.wang.fastfood.apicommons.entity.DTO;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 16:38
 * @Description:
 */

@Data
public class UserAccountDTO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 账户ID
     */
    private String accountId;
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 账户余额
     */
    private Double amount;
    /**
     * 用户等级
     */
    private Integer userLevel;
    /**
     * 用户经验
     */
    private Integer experience;
    /**
     * 该级别所需要的经验值
     */
    private Integer needExperience;
    /**
     * 账户积分
     */
    private Integer integral;

}
