package com.wang.fastfood.apicommons.entity.DTO;

import com.wang.fastfood.apicommons.entity.common.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 门店表
 * 
 * @author wAnG
 * @date 2022-01-08 16:16:51
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StoreDTO extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 门店经度
	 */
	private double storeLongitude;
	/**
	 * 门店纬度
	 */
	private double storeLatitude;
	/**
	 * 门店电话号码
	 */
	private String storePhoneNumber;
	/**
	 * 门店名称
	 */
	private String storeName;
	/**
	 * 门店地址
	 */
	private String address;
	/**
	 * 营业开始时间
	 */
	private String startTime;
	/**
	 * 营业结束时间
	 */
	private String endTime;
	/**
	 * 店长姓名
	 */
	private String managerName;
	/**
	 * 是否营业
	 */
	private boolean business;


}
