package com.wang.tradecenter.enums;

import lombok.AllArgsConstructor;

/**
 * @Auther: wAnG
 * @Date: 2022/4/10 18:58
 * @Description:
 */

@AllArgsConstructor
public enum PayMethod {

    WX_PAY(2,"微信支付"),
    FASTFOOD_PAY(1,"账户余额支付");

    private final Integer code;

    private final String payMethod;

    public Integer getCode() {
        return code;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public static String getPayMethodName(int code){
        for(PayMethod payMethod : PayMethod.values()){
            if(payMethod.getCode().equals(code)){
                return payMethod.getPayMethod();
            }
        }
        return null;
    }
}
