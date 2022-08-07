package com.wang.fastfood.apicommons.entity.DTO;


import lombok.Data;

/**
 * 外送地址
 */
@Data
public class TakeawayAddressDTO {

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



}
