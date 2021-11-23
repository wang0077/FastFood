package com.wang.fastfood.apicommons.entity.PO;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author wAnG
 * @Date  2021-11-24 00:15:32
 * @Description: 可选项种类（商品可选项,温度，甜度，配料等等）
 */
@Data
public class DetailTypePO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品详情分类ID
	 */
	private Integer id;
	/**
	 * 商品详情分类名称
	 */
	private String detailTypeName;

	private Date crateTime;

	private Date updateTime;

}
