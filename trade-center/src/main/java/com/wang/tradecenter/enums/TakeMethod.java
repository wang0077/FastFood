package com.wang.tradecenter.enums;

import lombok.AllArgsConstructor;

/**
 * @Auther: wAnG
 * @Date: 2022/4/10 18:54
 * @Description:
 */
@AllArgsConstructor
public enum TakeMethod {

    TAKE_IN(1,"自取"),

    TAKE_OUT(2,"外卖");

    private final Integer code;

    private final String methodName;

    public Integer getCode(){
        return this.code;
    }

    public String getMethodName() {
        return methodName;
    }

    public static String getOrderStatusName(int code){
        for(TakeMethod takeMethod : TakeMethod.values()){
            if(takeMethod.getCode().equals(code)){
                return takeMethod.getMethodName();
            }
        }
        return null;
    }
}
