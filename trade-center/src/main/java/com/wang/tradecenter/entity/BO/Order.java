package com.wang.tradecenter.entity.BO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wang.fastfood.apicommons.entity.DTO.OrderDTO;
import com.wang.fastfood.apicommons.entity.DTO.OrderDetail;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import com.wang.tradecenter.entity.PO.OrderPO;
import com.wang.tradecenter.enums.OrderStatus;
import com.wang.tradecenter.enums.PayMethod;
import com.wang.tradecenter.enums.TakeMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
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
public class Order extends Page {

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
	 * 订单商品详情
	 */
	private List<OrderDetail> orderDetail;
	/**
	 * 订单商品详情（JSON格式存储）
	 */
	private String orderDetailJson;
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

	public OrderPO doForward(){
		OrderPOConvert convert = new OrderPOConvert();
		return convert.convert(this);
	}

	public OrderDTO doBackward(){
		OrderDTOConvert convert = new OrderDTOConvert();
		return convert.convert(this);
	}

	private static class OrderPOConvert implements POConvert<Order, OrderPO> {

		@Override
		public OrderPO convert(Order order) {
			OrderPO orderPO = new OrderPO();
			String orderDetailJson = order.getOrderDetailJson();
			if(orderDetailJson != null){
				orderPO.setProductDetail(orderDetailJson);
			}
			BeanUtils.copyProperties(order,orderPO);
			return orderPO;
		}
	}

	private static class OrderDTOConvert implements DTOConvert<Order,OrderDTO> {

		@Override
		public OrderDTO convert(Order order) {
			OrderDTO orderDTO = new OrderDTO();
			if(order.getOrderTime() != null){
				orderDTO.setOrderTime(order.getOrderTime().toString());
			}
			if(order.getCompleteProductionTime() != null){
				orderDTO.setCompleteProductionTime(order.getCompleteProductionTime().toString());
			}
			if(order.getOrderStatus() != null){
				orderDTO.setOrderStatusName(OrderStatus.getOrderStatusName(order.getOrderStatus()));
			}
			if(order.getPayMethod() != null){
				orderDTO.setPayMethodName(PayMethod.getPayMethodName(order.getPayMethod()));
			}
			if(order.getTakeMethod() != null){
				orderDTO.setTakeMethodName(TakeMethod.getOrderStatusName(order.getTakeMethod()));
			}
			BeanUtils.copyProperties(order,orderDTO);
			return orderDTO;
		}
	}

}
