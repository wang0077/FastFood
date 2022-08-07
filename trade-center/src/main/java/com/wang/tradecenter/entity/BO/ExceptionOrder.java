package com.wang.tradecenter.entity.BO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wang.fastfood.apicommons.entity.DTO.ExceptionOrderDTO;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import com.wang.tradecenter.entity.PO.ExceptionOrderPO;
import com.wang.tradecenter.enums.TakeMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @Auther: wAnG
 * @Date: 2022/4/25 15:23
 * @Description:
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ExceptionOrder extends Page {

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
     * 电话号码
     */
    private String phoneNumber;
    /**
     * 用户名
     */
    private String UserName;
    /**
     * 商品订单详情（JSON格式）
     */
    private String OrderDetailJSON;
    /**
     * 取餐方式
     */
    private Integer takeMethod;
    /**
     * 订单金额
     */
    private Double orderAmount;
    /**
     * 取单号
     */
    private Integer takeOrderNumber;
    /**
     * 下单时间
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime orderTime;
    /**
     * 异常信息
     */
    private String exceptionInfo;

    public ExceptionOrderPO doForward(){
        exceptionOrderPOConvert convert = new exceptionOrderPOConvert();
        return convert.convert(this);
    }

    public ExceptionOrderDTO doBackward(){
        exceptionOrderDTOConvert convert = new exceptionOrderDTOConvert();
        return convert.convert(this);
    }

    private static class exceptionOrderPOConvert implements POConvert<ExceptionOrder, ExceptionOrderPO> {

        @Override
        public ExceptionOrderPO convert(ExceptionOrder exceptionOrder) {
            ExceptionOrderPO exceptionOrderPO = new ExceptionOrderPO();
            BeanUtils.copyProperties(exceptionOrder,exceptionOrderPO);
            return exceptionOrderPO;
        }
    }

    private static class exceptionOrderDTOConvert implements DTOConvert<ExceptionOrder, ExceptionOrderDTO> {
        @Override
        public ExceptionOrderDTO convert(ExceptionOrder exceptionOrder) {
            ExceptionOrderDTO exceptionOrderDTO = new ExceptionOrderDTO();
            if(exceptionOrder.getTakeMethod() != null){
                exceptionOrderDTO.setTakeMethodName(TakeMethod.getOrderStatusName(exceptionOrder.getTakeMethod()));
            }
            if(exceptionOrder.getOrderTime() != null){
                exceptionOrderDTO.setOrderTime(exceptionOrder.getOrderTime().toString().replace("T"," "));
            }
            BeanUtils.copyProperties(exceptionOrder,exceptionOrderDTO);
            return exceptionOrderDTO;
        }
    }

}
