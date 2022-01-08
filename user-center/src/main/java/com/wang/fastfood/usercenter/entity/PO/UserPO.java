package com.wang.fastfood.usercenter.entity.PO;

import com.wang.fastfood.apicommons.entity.common.BasePO;
import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import com.wang.fastfood.usercenter.entity.BO.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * 
 * @author wAnG@Date: 2021/11/28 17:31
 * @date 2022-01-02 20:20:56
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserPO extends BasePO implements Serializable {
	private static final long serialVersionUID = 1L;

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
	 * 生日
	 */
	private Date birthday;
	/**
	 * 用户入会时间
	 */
	private Date joinTime;
	/**
	 * 用户等级
	 */
	private Integer userLevel;
	/**
	 * 用户经验值
	 */
	private Integer experience;
	/**
	 * 数据是否有效
	 */
	private Integer valid;
	/**
	 * 
	 */
	private Date crateTime;
	/**
	 * 
	 */
	private Date updateTime;

	public User convertToDetailType(){
		userBOConvert convert = new userBOConvert();
		return convert.convert(this);
	}

	private static class userBOConvert implements BOConvert<UserPO, User> {

		@Override
		public User convert(UserPO userPO) {
			User user = new User();
			BeanUtils.copyProperties(userPO,user);
			return user;
		}
	}

}
