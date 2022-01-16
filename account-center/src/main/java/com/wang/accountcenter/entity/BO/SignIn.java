package com.wang.accountcenter.entity.BO;

import com.wang.accountcenter.entity.PO.SignInPO;
import com.wang.fastfood.apicommons.entity.DTO.SignInDTO;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 20:46
 * @Description:
 */

@Data
public class SignIn {

    /**
     * 主键
     */
    private Long id;
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 最后一次签到时间
     */
    private LocalDateTime lastTime;
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

    public SignInPO doForward(){
        signInPOConvert convert = new signInPOConvert();
        return convert.convert(this);
    }

    public SignInDTO doBackward(){
        signInDTOConvert convert = new signInDTOConvert();
        return convert.convert(this);
    }


    private static class signInPOConvert implements POConvert<SignIn, SignInPO> {

        @Override
        public SignInPO convert(SignIn signIn) {
            SignInPO signInPO = new SignInPO();
            BeanUtils.copyProperties(signIn,signInPO);
            return signInPO;
        }
    }

    private static class signInDTOConvert implements DTOConvert<SignIn, SignInDTO> {

        @Override
        public SignInDTO convert(SignIn signIn) {
            SignInDTO signInDTO = new SignInDTO();
            BeanUtils.copyProperties(signIn,signInDTO);
            return signInDTO;
        }
    }

}
