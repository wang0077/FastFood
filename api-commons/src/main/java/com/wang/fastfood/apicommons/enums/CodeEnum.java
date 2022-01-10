package com.wang.fastfood.apicommons.enums;

/**
 * @Auther: wAnG
 * @Date: 2021/11/23 21:38
 * @Description:
 */
public enum CodeEnum implements BaseEnum {
    // [10000,19999] 表示操作成功
    SUCCESS(10000, "成功"),
    LOGIN_SUCCESS(10001,"登录成功"),
    SQL_INSERT_SUCCESS(11001,"添加成功"),
    SQL_UPDATE_SUCCESS(11002,"更新成功"),
    SQL_DELETE_SUCCESS(11003,"删除成功"),

    // [20000,29999] 表示系统错误
    SERVER_ERROR(20000, "服务器异常"),
    // [30000,39999] 表示数据库错误
    SQL_ERROR(30000,"数据库异常"),
    SQL_INSERT_ERROR(30001,"数据添加异常"),
    SQL_REPEAT_INSERT_ERROR(30002,"数据重复添加"),
    SQL_UPDATE_ERROR(30010,"数据更新异常"),
    SQL_DELETE_ERROR(30020,"数据删除异常"),
    // [40000,49999] 表示请求异常
    ILLEGAL_REQUEST(40000,"非法请求");

    private final int code;

    private final String msg;

    CodeEnum(int code, String msg) {
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
