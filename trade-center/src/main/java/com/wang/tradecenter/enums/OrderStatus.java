package com.wang.tradecenter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {
    /**
     * 未支付
     */
    NOTPAY(1,"未支付"),


    /**
     * 支付成功
     */
    PAY_SUCCESS(2,"支付成功"),

    /**
     * 制作中
     */
    MAKE(3,"制作中"),


    /**
     * 请取餐
     */
    PLEASE_PICK_UP(5,"请取餐"),

    /**
     * 配送中
     */
    IN_DELIVERY(4,"配送中"),

    /**
     * 订单完成
     */
    ORDER_SUCCESS(7,"订单完成"),

    /**
     * 超时已关闭
     */
    CLOSED(10,"超时已关闭"),

    /**
     * 退款中
     */
    REFUND_PROCESSING(15,"退款中"),

    /**
     * 已退款
     */
    REFUND_SUCCESS(16,"已退款"),

    /**
     * 退款异常
     */
    REFUND_REFUND_ABNORMAL(17,"退款异常"),

    /**
     * 订单结束
     */
    ORDER_END(20,"订单结束"),

    /**
     * 订单异常
     */
    ORDER_REFUND_ABNORMAL(30,"订单异常");


    /**
     *  状态码
     */
    private final Integer code;

    public Integer getCode(){
        return this.code;
    }

    /**
     * 类型
     */
    private final String type;

    public String getType(){
        return this.type;
    }

    public static String getOrderStatusName(int code){
        for(OrderStatus orderStatus : OrderStatus.values()){
            if(orderStatus.getCode().equals(code)){
                return orderStatus.getType();
            }
        }
        return null;
    }
}
