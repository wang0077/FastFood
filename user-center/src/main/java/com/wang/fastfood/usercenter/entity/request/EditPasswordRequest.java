package com.wang.fastfood.usercenter.entity.request;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/4/24 16:36
 * @Description:
 */

@Data
public class EditPasswordRequest {
    private String userName;

    private String oldPassword;

    private String curPassword;
}
