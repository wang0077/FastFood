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
     * 账户积分
     */
    private Integer integral;

}
