package com.wang.tradecenter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Auther: wAnG
 * @Date: 2022/4/25 15:38
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum OrderExceptionEnum {

    PICK_UP_TIME_OUT(1,"超时未取餐"),
    NO_PAY_TIME_OUT(3,"超时未支付"),
    NO_MAKE_TIME_OUT(4,"超时未制作"),
    TAKE_CODE_EXCEPTION(5,"取餐码异常"),
    UNKNOWN_EXCEPTION(10,"未知异常");

    private final Integer code;

    private final String type;

    public static String getOrderExceptionName(int code){
        for(OrderExceptionEnum exceptionEnum : OrderExceptionEnum.values()){
            if(exceptionEnum.getCode().equals(code)){
                return exceptionEnum.getType();
            }
        }
        return null;
    }

}
