package com.wang.fastfoodapi.entity.Request;

import com.google.common.base.Strings;
import com.wang.fastfood.apicommons.entity.common.BaseRequest;
import com.wang.fastfood.apicommons.exception.ParamException;

import javax.validation.constraints.NotBlank;

/**
 * @Auther: wAnG
 * @Date: 2022/1/5 21:17
 * @Description:
 */

public class UserRegisterRequest extends BaseRequest {

    @NotBlank(message = "userName不能为空")
    private String userName;

    @NotBlank(message = "password不能为空")
    private String passWord;


    @Override
    public void validity() {
        if(Strings.isNullOrEmpty(userName)){
            throw new ParamException("UserName");
        }
    }
}
