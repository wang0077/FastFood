package com.wang.accountcenter.entity.BO;

import com.wang.accountcenter.entity.PO.UserAccountPO;
import com.wang.fastfood.apicommons.entity.DTO.UserAccountDTO;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 16:29
 * @Description:
 */

@Data
public class UserAccount {

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
    /**
     * 新增积分
     */
    private Integer addIntegral;
    /**
     * 签到信息
     */
    private SignIn signIn;

    public UserAccountPO doForward(){
        UserAccountPOConvert convert = new UserAccountPOConvert();
        return convert.convert(this);
    }

    public UserAccountDTO doBackward(){
        UserAccountDTOConvert convert = new UserAccountDTOConvert();
        return convert.convert(this);
    }


    private static class UserAccountPOConvert implements POConvert<UserAccount, UserAccountPO> {

        @Override
        public UserAccountPO convert(UserAccount userAccount) {
            UserAccountPO userAccountPO = new UserAccountPO();
            BeanUtils.copyProperties(userAccount,userAccountPO);
            return userAccountPO;
        }
    }

    private static class UserAccountDTOConvert implements DTOConvert<UserAccount, UserAccountDTO> {

        @Override
        public UserAccountDTO convert(UserAccount userAccount) {
            UserAccountDTO userAccountDTO = new UserAccountDTO();
            BeanUtils.copyProperties(userAccount,userAccountDTO);
            return userAccountDTO;
        }
    }

}
