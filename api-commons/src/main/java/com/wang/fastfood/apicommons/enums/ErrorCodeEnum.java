package com.wang.fastfood.apicommons.enums;

/**
 * @Auther: wAnG
 * @Date: 2021/11/23 21:38
 * @Description:
 */
public enum ErrorCodeEnum implements BaseEnum {
    // [10000,19999] 表示操作成功
    SUCCESS(10000, "成功"),

    // [20000,29999] 表示系统错误
    SERVER_ERROR(20000, "服务器异常"),
    SQL_ERROR(30000,"数据库异常"),
    SQL_INSERT_ERROR(30001,"数据添加异常"),
    SQL_REPEAT_INSERT_ERROR(30002,"数据重复添加"),
    SQL_UPDATE_ERROR(30010,"数据更新异常");

    private final int code;

    private final String msg;

    ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
