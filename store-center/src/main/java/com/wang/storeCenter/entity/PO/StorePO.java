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

	public Store convertToDetailType(){
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
