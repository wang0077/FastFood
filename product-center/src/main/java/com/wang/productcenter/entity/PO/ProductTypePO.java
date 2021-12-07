package com.wang.productcenter.entity.PO;


import com.wang.fastfood.apicommons.entity.common.BasePO;
import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import com.wang.productcenter.entity.BO.ProductType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author wAnG
 * @Date 2021-11-24 00:15:32
 * @Description: 商品类型表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductTypePO extends BasePO implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * 种类ID
	 */
	private Integer id;
	/**
	 * 种类名称
	 */
	private String name;

	/**
	 * 数据是否有效
	 */
	private Integer valid;

	private Date crateTime;

	private Date updateTime;

	public ProductType convertToProductType(){
		ProductTypeBOConvert productTypeBOConvert = new ProductTypeBOConvert();
		return productTypeBOConvert.convert(this);
	}

	private static class ProductTypeBOConvert implements BOConvert<ProductTypePO, ProductType> {

		@Override
		public ProductType convert(ProductTypePO productTypePO) {
			ProductType productType = new ProductType();
			BeanUtils.copyProperties(productTypePO,productType);
			return productType;
		}
	}
}
