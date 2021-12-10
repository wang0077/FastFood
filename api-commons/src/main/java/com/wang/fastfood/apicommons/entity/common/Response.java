package com.wang.fastfood.apicommons.entity.common;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2021/11/23 21:25
 * @Description:
 */

@Data
public class Response<T> {
    /**
     * 响应码
     */
    private int code;

    /**
     * 响应码对应的内容
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    public Response(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getErrorCode() {
        return code;
    }

    public void setErrorCode(int errorCode) {
        this.code = errorCode;
    }

    public String getErrorMsg() {
        return msg;
    }

    public void setErrorMsg(String errorMsg) {
        this.msg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
