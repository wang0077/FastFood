package com.wang.tradecenter.entity.PO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import com.wang.tradecenter.entity.BO.Order;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 订单表
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-03-20 17:24:31
 */
@Data
public class OrderPO implements Serializable {
	private static final long serialVersionUID = 1L;

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
	 * 取餐方式
	 */
	private Integer takeMethod;
	/**
	 * 支付方式
	 */
	private Integer payMethod;
	/**
	 * 订单商品详情（JSON格式存储）
	 */
	private String productDetail;
	/**
	 * 订单金额
	 */
	private Double orderAmount;
	/**
	 * 订单状态
	 */
	private Integer orderStatus;
	/**
	 * 取单号
	 */
	private Integer takeOrderNumber;
	/**
	 * 完成制作时间
	 */
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime completeProductionTime;
	/**
	 * 下单时间
	 */
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime orderTime;
	/**
	 * 数据是否有效
	 */
	private Integer valid;
	/**
	 * 
	 */
	private Date crateTime;
	/**
	 * 
	 */
	private Date updateTime;

	public Order convertToOrder(){
		orderBOConvert convert = new orderBOConvert();
		return convert.convert(this);
	}

	private static class orderBOConvert implements BOConvert<OrderPO, Order> {

		@Override
		public Order convert(OrderPO orderPO) {
			Order order = new Order();
			if(orderPO.getProductDetail() != null){
				order.setOrderDetailJson(orderPO.getProductDetail());
			}
			BeanUtils.copyProperties(orderPO,order);
			return order;
		}
	}

}
