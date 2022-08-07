package com.wang.tradecenter.entity.BO;


import com.wang.fastfood.apicommons.entity.DTO.StoreSalesInfoDTO;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import com.wang.tradecenter.entity.PO.StoreSalesInfoPO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class StoreSalesInfo {

	/**
	 * 主键ID
	 */
	private Integer id;
	/**
	 * 门店ID
	 */
	private String storeId;
	/**
	 * 销售额
	 */
	private Double sales;
	/**
	 * 微信支付数量
	 */
	private Integer wxPayCount;
	/**
	 * 账户支付数量
	 */
	private Integer fastFoodPayCount;
	/**
	 * 自取数量
	 */
	private Integer pickUpCount;
	/**
	 * 外送数量
	 */
	private Integer deliveryCount;
	/**
	 * 数据是否有效
	 */
	private Integer valid;
	/**
	 * 
	 */
	private LocalDate DataTime;


	public StoreSalesInfoPO doForward(){
		StoreSalesInfoPOConvert convert = new StoreSalesInfoPOConvert();
		return convert.convert(this);
	}

	public StoreSalesInfoDTO doBackward(){
		StoreSalesInfoDTOConvert convert = new StoreSalesInfoDTOConvert();
		return convert.convert(this);
	}

	private static class StoreSalesInfoPOConvert implements POConvert<StoreSalesInfo, StoreSalesInfoPO> {
		@Override
		public StoreSalesInfoPO convert(StoreSalesInfo storeSalesInfo) {
			StoreSalesInfoPO storeSalesInfoPO = new StoreSalesInfoPO();
			BeanUtils.copyProperties(storeSalesInfo,storeSalesInfoPO);
			return storeSalesInfoPO;
		}
	}

	private static class StoreSalesInfoDTOConvert implements DTOConvert<StoreSalesInfo, StoreSalesInfoDTO> {

		private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");

		@Override
		public StoreSalesInfoDTO convert(StoreSalesInfo storeSalesInfo) {
			StoreSalesInfoDTO storeSalesInfoDTO = new StoreSalesInfoDTO();
			if(storeSalesInfo.DataTime != null){
				storeSalesInfoDTO.setDataTime(storeSalesInfo.DataTime.format(fmt));
			}
			BeanUtils.copyProperties(storeSalesInfo,storeSalesInfoDTO);
			return storeSalesInfoDTO;
		}
	}

}
