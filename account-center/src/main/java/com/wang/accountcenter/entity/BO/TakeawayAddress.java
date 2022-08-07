package com.wang.accountcenter.entity.BO;


import com.wang.accountcenter.entity.PO.TakeawayAddressPO;
import com.wang.fastfood.apicommons.entity.DTO.TakeawayAddressDTO;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 外送地址
 */
@Data
public class TakeawayAddress {

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

	public TakeawayAddressPO doForward(){
		TakeawayAddressPOConvert convert = new TakeawayAddressPOConvert();
		return convert.convert(this);
	}

	public TakeawayAddressDTO doBackward(){
		TakeawayAddressDTOConvert convert = new TakeawayAddressDTOConvert();
		return convert.convert(this);
	}


	private static class TakeawayAddressPOConvert implements POConvert<TakeawayAddress, TakeawayAddressPO> {

		@Override
		public TakeawayAddressPO convert(TakeawayAddress takeawayAddress) {
			TakeawayAddressPO takeawayAddressPO = new TakeawayAddressPO();
			BeanUtils.copyProperties(takeawayAddress,takeawayAddressPO);
			return takeawayAddressPO;
		}
	}

	private static class TakeawayAddressDTOConvert implements DTOConvert<TakeawayAddress, TakeawayAddressDTO> {

		@Override
		public TakeawayAddressDTO convert(TakeawayAddress takeawayAddress) {
			TakeawayAddressDTO takeawayAddressDTO = new TakeawayAddressDTO();
			BeanUtils.copyProperties(takeawayAddress,takeawayAddressDTO);
			return takeawayAddressDTO;
		}
	}

}
