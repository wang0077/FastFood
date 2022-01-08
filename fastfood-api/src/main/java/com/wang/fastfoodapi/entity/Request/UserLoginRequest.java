package com.wang.fastfoodapi.entity.Request;

import com.google.common.base.Strings;
import com.wang.fastfood.apicommons.entity.common.BaseRequest;
import com.wang.fastfood.apicommons.exception.ParamException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Auther: wAnG
 * @Date: 2021/11/23 21:51
 * @Description:
 */

@Getter
@Setter
@ToString
public class UserLoginRequest extends BaseRequest {

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 手机号
     */
    private String telephone;



    @Override
    public void validity() {
        try {
            if(Strings.isNullOrEmpty(verifyCode)){
                throw new ParamException("参数verifyCode为空");
            }
            if(Strings.isNullOrEmpty(telephone)){
                throw new ParamException("参数telephone为空");
            }
        }catch (ParamException e){
            e.printStackTrace();
        }
    }
}
