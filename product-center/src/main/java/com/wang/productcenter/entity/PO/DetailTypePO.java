package com.wang.productcenter.entity.PO;


import com.wang.fastfood.apicommons.entity.common.BasePO;
import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import com.wang.productcenter.entity.BO.DetailType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author wAnG
 * @Date  2021-11-24 00:15:32
 * @Description: 可选项种类（商品可选项,温度，甜度，配料等等）
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DetailTypePO extends BasePO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品详情分类ID
	 */
	private Integer id;

	/**
	 * 商品详情ID
	 */
	private Integer productDetailId;

	/**
	 * 商品详情分类名称
	 */
	private String detailTypeName;

	/**
	 * 数据是否有效
	 */
	private Integer valid;

	private Date crateTime;

	private Date updateTime;

	public DetailType convertToDetailType(){
		DetailTypeBOConvert convert = new DetailTypeBOConvert();
		return convert.convert(this);
	}

	private static class DetailTypeBOConvert implements BOConvert<DetailTypePO, DetailType>{

		@Override
		public DetailType convert(DetailTypePO detailTypePO) {
			DetailType detailType = new DetailType();
			BeanUtils.copyProperties(detailTypePO,detailType);
			return detailType;
		}
	}

}
