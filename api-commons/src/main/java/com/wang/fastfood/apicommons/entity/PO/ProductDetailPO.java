package com.wang.fastfood.apicommons.entity.PO;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

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
	 * 商品详情种类ID
	 */
	private Integer detailTypeId;

	/**
	 * 商品详情名称
	 */
	private String productDetailName;

	/**
	 * 可选种类列表
	 */
	private List<DetailTypePO> detailTypePOList;

	private Date crateTime;

	private Date updateTime;

}
