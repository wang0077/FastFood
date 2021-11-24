package com.wang.fastfood.apicommons.entity.PO;


import lombok.Data;

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
public class ProductTypePO implements Serializable {
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
	 * 
	 */
	private Date crateTime;
	/**
	 * 
	 */
	private Date updateTime;

}
