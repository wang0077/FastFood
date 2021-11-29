package com.wang.productcenter.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品表
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-11-27 02:06:31
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
	private Integer typeId;
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
	 * 数据是否有效
	 */
	private Integer valid;

	private Date crateTime;

	private Date updateTime;

	private String clazz;

}
