package com.wang.productcenter.entity.PO;

import com.wang.fastfood.apicommons.entity.common.BasePO;
import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import com.wang.productcenter.entity.BO.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品表
 *
 * @author wAnG
 * @Date 2021-11-24 00:15:32
 * @Description: 商品表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductPO extends BasePO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品ID
	 */
	private Integer id;

	/**
	 * 种类ID
	 */
	private Integer typeId;

	/**
	 * 商品名称
	 */
	private String productName;

	/**
	 * 商品价格
	 */
	private Double productPrice;

	/**
	 * 商品图片
	 */
	private String productImagePath;
	/**
	 * 商品销量
	 */
	private Integer sales;

	/**
	 * 商品是否上架
	 */
	private Integer isSales;

	/**
	 * 商品描述
	 */
	private String describe;

	/**
	 * 数据是否有效
	 */
	private Integer valid;

	/**
	 * 商品种类
	 */
	private ProductTypePO productType;

	private Date crateTime;

	private Date updateTime;


	public Product convertToProduct(){
		ProductBOConvert convert = new ProductBOConvert();
		return convert.convert(this);
	}

	private static class ProductBOConvert implements BOConvert<ProductPO, Product> {

		@Override
		public Product convert(ProductPO productPO) {
			Product product = new Product();
			BeanUtils.copyProperties(productPO,product);
			return product;
		}
	}
}
