package com.wang.fastfood.usercenter.entity.request;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/4/11 03:04
 * @Description:
 */

@Data
public class WXSaveUserInfoRequest {
    private String uid;

    private String phone;

    private String birthday;

    private Integer sex;

    private String userName;

}
