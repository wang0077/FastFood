package com.wang.productcenter.entity.PO;


import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import com.wang.productcenter.entity.BO.ProductDetail;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author wAnG
 * @Date 2021-11-24 00:15:32
 * @Description: 商品详情（商品可选项，如温度[去冰，少冰....]，甜度[三分糖，五分糖....]等等）
 */
@Data
public class ProductDetailPO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品详情ID
	 */
	private Integer id;

	/**
	 * 商品详情名称
	 */
	private String productDetailName;


	/**
	 * 数据是否有效
	 */
	private Integer valid;

	private Date crateTime;

	private Date updateTime;

	public ProductDetail convertToProductDetail(){
		ProductDetailBOConvert convert = new ProductDetailBOConvert();
		return convert.convert(this);
	}

	private static class ProductDetailBOConvert implements BOConvert<ProductDetailPO, ProductDetail> {

		@Override
		public ProductDetail convert(ProductDetailPO productDetailPO) {
			ProductDetail productDetail = new ProductDetail();
			BeanUtils.copyProperties(productDetailPO,productDetail);
			return productDetail;
		}
	}

}
