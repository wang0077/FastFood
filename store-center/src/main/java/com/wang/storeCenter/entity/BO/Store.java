package com.wang.storeCenter.entity.BO;

import com.wang.fastfood.apicommons.entity.DTO.StoreDTO;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import com.wang.storeCenter.entity.PO.StorePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * 门店表
 * 
 * @author wAnG
 * @date 2022-01-08 16:16:51
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Store extends Page {
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

	public StorePO doForward(){
		storePOConvert convert = new storePOConvert();
		return convert.convert(this);
	}

	public StoreDTO doBackward(){
		storeDTOConvert convert = new storeDTOConvert();
		return convert.convert(this);
	}


	private static class storePOConvert implements POConvert<Store, StorePO> {

		@Override
		public StorePO convert(Store store) {
			StorePO storePO = new StorePO();
			BeanUtils.copyProperties(store,storePO);
			return storePO;
		}
	}

	private static class storeDTOConvert implements DTOConvert<Store, StoreDTO> {

		@Override
		public StoreDTO convert(Store store) {
			StoreDTO storeDTO = new StoreDTO();
			BeanUtils.copyProperties(store,storeDTO);
			return storeDTO;
		}
	}

}
