package com.wang.fastfood.usercenter.entity.BO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import com.wang.fastfood.usercenter.entity.PO.UserPO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Auther: wAnG
 * @Date: 2022/1/2 20:23
 * @Description:
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends Page {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
    /**
     * 电话号码
     */
    private String phoneNumber;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 生日
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDate birthday;
    /**
     * 用户入会时间
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime joinTime;
    /**
     * 用户权限
     */
    private Integer role;
    /**
     * 用户等级
     */
    private Integer userLevel;
    /**
     * 用户经验值
     */
    private Integer experience;
    /**
     * 门店ID
     */
    private String storeId;

    public UserPO doForward(){
        UserPOConvert convert = new UserPOConvert();
        return convert.convert(this);
    }

    public UserDTO doBackward(){
        UserDTOConvert convert = new UserDTOConvert();
        return convert.convert(this);
    }


    private static class UserPOConvert implements POConvert<User, UserPO> {

        @Override
        public UserPO convert(User user) {
            UserPO userPO = new UserPO();
            BeanUtils.copyProperties(user,userPO);
            return userPO;
        }
    }

    private static class UserDTOConvert implements DTOConvert<User, UserDTO> {

        @Override
        public UserDTO convert(User user) {
            UserDTO userDTO = new UserDTO();
            if(user.getJoinTime() != null){
                userDTO.setJoinTime(user.getJoinTime().toString().replace("T"," "));
            }
            if(user.getBirthday() != null){
                userDTO.setBirthday(user.getBirthday().toString().replace("T"," "));
            }
            BeanUtils.copyProperties(user, userDTO);
            return userDTO;
        }
    }
}
