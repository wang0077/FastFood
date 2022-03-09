package com.wang.storeCenter.entity.PO;

import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import com.wang.storeCenter.entity.BO.Store;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 门店表
 * 
 * @author wAnG
 * @date 2022-01-08 16:16:51
 */
@Data
public class StorePO implements Serializable {
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

	public Store convertToStore(){
		storeBOConvert convert = new storeBOConvert();
		return convert.convert(this);
	}

	private static class storeBOConvert implements BOConvert<StorePO, Store> {

		@Override
		public Store convert(StorePO storePO) {
			Store store = new Store();
			BeanUtils.copyProperties(storePO,store);
			return store;
		}
	}

}
