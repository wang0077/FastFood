package com.wang.fastfoodapi.entity.Request;

import com.google.common.base.Strings;
import com.wang.fastfood.apicommons.entity.BO.User;
import com.wang.fastfoodapi.Exception.ParamException;
import com.wang.fastfoodapi.entity.common.BaseRequest;
import com.wang.fastfoodapi.entity.common.DTOConvert;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

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


    public User convertToUser(){
        UserDTOConvert userDTOConvert = new UserDTOConvert();
        return userDTOConvert.convert(this);
    }

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


    private static class UserDTOConvert implements DTOConvert<UserLoginRequest,User> {
        @Override
        public User convert(UserLoginRequest userRequest) {
            User user = new User();
            BeanUtils.copyProperties(userRequest,user);
            return user;
        }
    }
}
