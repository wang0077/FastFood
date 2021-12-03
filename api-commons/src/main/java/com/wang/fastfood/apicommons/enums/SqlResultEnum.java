package com.wang.fastfood.apicommons.enums;

/**
 * @Auther: wAnG
 * @Date: 2021/12/3 01:27
 * @Description:
 */
public enum SqlResultEnum {

    ERROR_INSERT(0,"添加错误"),
    REPEAT_INSERT(-1,"重复添加");

    private final int value;

    SqlResultEnum(int value,String errorName){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
