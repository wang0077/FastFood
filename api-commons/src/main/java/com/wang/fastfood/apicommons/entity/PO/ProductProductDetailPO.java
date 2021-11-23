package com.wang.fastfood.apicommons.entity.PO;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author wAnG
 * @Date 2021-11-24 00:15:32
 * @Description: 商品和商品详情中间表
 */
@Data
public class ProductProductDetailPO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品ID
	 */
	private Integer pid;
	/**
	 * 商品详情ID
	 */
	private Integer pdid;

	private Date crateTime;

	private Date updateTime;

}
