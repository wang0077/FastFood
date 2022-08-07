package com.wang.fastfood.apicommons.entity.DTO;

import com.wang.fastfood.apicommons.entity.common.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 订单表
 * 
 * @author wAnG
 * @email 22891399@qq.com
 * @date 2022-03-20 17:24:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDTO extends BaseRequest {

	public OrderDTO(){

	}

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 订单ID
	 */
	private String orderId;
	/**
	 * 门店ID
	 */
	private String storeId;
	/**
	 * 用户ID
	 */
	private String uid;
	/**
	 * 订单商品详情（JSON格式存储）
	 */
	private String orderDetailJson;
	/**
	 * 取餐方式
	 */
	private Integer takeMethod;
	/**
	 * 取餐方式名称
	 */
	private String takeMethodName;
	/**
	 * 支付方式名称
	 */
	private String payMethodName;
	/**
	 * 支付方式
	 */
	private Integer payMethod;
	/**
	 * 订单商品详情
	 */
	private List<OrderDetail> orderDetail;

	/**
	 * 订单金额
	 */
	private Double orderAmount;
	/**
	 * 订单状态
	 */
	private Integer orderStatus;
	/**
	 * 订单状态名字
	 */
	private String orderStatusName;
	/**
	 * 取单号
	 */
	private Integer takeOrderNumber;
	/**
	 * 完成制作时间
	 */
	private String completeProductionTime;
	/**
	 * 下单时间
	 */
	private String orderTime;
	/**
	 * 用户等级
	 */
	private Integer userLevel;
	/**
	 * 用户经验值
	 */
	private Integer experience;

}
