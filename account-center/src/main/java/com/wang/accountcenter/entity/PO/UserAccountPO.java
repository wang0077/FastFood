package com.wang.accountcenter.entity.PO;

import com.wang.accountcenter.entity.BO.UserAccount;
import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 账户表(用户钱包优惠券等信息)
 * 
 * @author wAnG
 * @date 2022-01-15 16:23:10
 */
@Data
public class UserAccountPO implements Serializable {
	private static final long serialVersionUID = 1L;

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

	public UserAccount convertToUserAccount(){
		UserAccountBOConvert convert = new UserAccountBOConvert();
		return convert.convert(this);
	}

	private static class UserAccountBOConvert implements BOConvert<UserAccountPO, UserAccount> {

		@Override
		public UserAccount convert(UserAccountPO userAccountPO) {
			UserAccount userAccount = new UserAccount();
			BeanUtils.copyProperties(userAccountPO,userAccount);
			return userAccount;
		}
	}

}
