package com.wang.tradecenter.entity.PO;

import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import com.wang.tradecenter.entity.BO.ExceptionOrder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;


@Data
public class ExceptionOrderPO {

	/**
	 * 主键ID
	 */
	private Integer id;
	/**
	 * 门店ID
	 */
	private Integer storeId;
	/**
	 * 订单ID
	 */
	private String orderId;
	/**
	 * 用户ID
	 */
	private String uid;
	/**
	 * 取餐方式
	 */
	private Integer takeMethod;
	/**
	 * 订单金额
	 */
	private Double orderAmount;
	/**
	 * 异常信息
	 */
	private String exceptionInfo;
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

	public ExceptionOrder convertToOrder(){
		exceptionOrderBOConvert convert = new exceptionOrderBOConvert();
		return convert.convert(this);
	}

	private static class exceptionOrderBOConvert implements BOConvert<ExceptionOrderPO, ExceptionOrder> {

		@Override
		public ExceptionOrder convert(ExceptionOrderPO exceptionOrderPO) {
			ExceptionOrder exceptionOrder = new ExceptionOrder();
			BeanUtils.copyProperties(exceptionOrderPO,exceptionOrder);
			return exceptionOrder;
		}
	}

}
