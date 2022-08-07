package com.wang.accountcenter.entity.PO;


import com.wang.accountcenter.entity.BO.TakeawayAddress;
import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 外送地址表
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-04-12 02:38:28
 */
@Data
public class TakeawayAddressPO {

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 外送地址ID
	 */
	private String addressId;
	/**
	 * 用户ID
	 */
	private String uid;
	/**
	 * 电话号码
	 */
	private String phoneNumber;
	/**
	 * 性别
	 */
	private Integer sex;
	/**
	 * 收货人姓名
	 */
	private String name;
	/**
	 * 收货地址
	 */
	private String address;
	/**
	 * 门牌号
	 */
	private String houseNumber;
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

	public TakeawayAddress convertToTakeawayAddress(){
		TakeawayAddressBOConvert convert = new TakeawayAddressBOConvert();
		return convert.convert(this);
	}

	private static class TakeawayAddressBOConvert implements BOConvert<TakeawayAddressPO, TakeawayAddress> {

		@Override
		public TakeawayAddress convert(TakeawayAddressPO takeawayAddressPO) {
			TakeawayAddress takeawayAddress = new TakeawayAddress();
			BeanUtils.copyProperties(takeawayAddressPO,takeawayAddress);
			return takeawayAddress;
		}
	}

}
