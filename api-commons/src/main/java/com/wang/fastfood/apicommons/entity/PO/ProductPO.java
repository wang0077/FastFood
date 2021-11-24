package com.wang.fastfood.apicommons.entity.PO;


import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
 * 
 * @author wAnG
 * @Date 2021-11-24 00:15:32
 * @Description: 商品表
 */
@Data
public class ProductPO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品ID
	 */
	private Integer id;
	/**
	 * 种类ID
	 */
	private Integer TypeId;
	/**
	 * 商品详情ID
	 */
	private Integer productProductDetailId;
	/**
	 * 商品名称
	 */
	private String productName;
	/**
	 * 商品价格
	 */
	private BigDecimal productPrice;
	/**
	 * 商品图片
	 */
	private String productImage;
	/**
	 * 商品销量
	 */
	private Integer sales;
	/**
	 * 商品是否上架
	 */
	private Integer isSales;

	/**
	 * 商品详情列表
	 */
	private List<ProductDetailPO> productDetailPOList;

	private Date crateTime;

	private Date updateTime;

}
