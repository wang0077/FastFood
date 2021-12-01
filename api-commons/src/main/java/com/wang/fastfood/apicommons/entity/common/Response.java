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
    private int errorCode;

    /**
     * 响应码对应的内容
     */
    private String Message;

    /**
     * 响应数据
     */
    private T data;

    public Response(Integer code, String msg, T data) {
        this.errorCode = code;
        this.Message = msg;
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return Message;
    }

    public void setErrorMsg(String errorMsg) {
        this.Message = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
