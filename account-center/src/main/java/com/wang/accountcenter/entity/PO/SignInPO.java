package com.wang.accountcenter.entity.PO;

import com.wang.accountcenter.entity.BO.SignIn;
import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 * 
 * @author chenshun
 * @date 2022-01-15 20:42:57
 * @Description: 签到信息实体
 */
@Data
public class SignInPO implements Serializable {
	private static final long serialVersionUID = 1L;

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

	public SignIn convertToSignIn(){
		SignInBOConvert convert = new SignInBOConvert();
		return convert.convert(this);
	}

	private static class SignInBOConvert implements BOConvert<SignInPO, SignIn> {

		@Override
		public SignIn convert(SignInPO signInPO) {
			SignIn sign = new SignIn();
			BeanUtils.copyProperties(signInPO,sign);
			return sign;
		}
	}

}
