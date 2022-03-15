package com.wang.fastfood.apicommons.Util;

import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.BaseEnum;
import com.wang.fastfood.apicommons.enums.CodeEnum;


/**
 * @Auther: wAnG
 * @Date: 2021/11/23 21:29
 * @Description:
 */
@SuppressWarnings("all")
public class ResponseUtil {

    public static Response success() {
        return buildResult(CodeEnum.SUCCESS, null);
    }

    public static<T> Response<T> success(T data) {
        return buildResult(CodeEnum.SUCCESS, data);
    }

    public static<T> Response<T> success(BaseEnum code) {
        return buildResult(CodeEnum.SUCCESS,null);
    }

    public static<T> Response<T> success(BaseEnum errorCode, T data) {
        return buildResult(errorCode, data);
    }

    @Deprecated
    public static<T> Response<T> fail(BaseEnum errorCode,T data) {
        return buildResult(errorCode, data);
    }

    public static<T> Response<T> fail(BaseEnum errorCode) {
        return buildResult(errorCode, null);
    }

    public static<T> Response<T> fail(int errorCode, String errorMsg) {
        return buildResult(errorCode, errorMsg, null);
    }

    public static<T> Response<T> fail(BaseEnum errorCode,String errorMsg){
        return buildResult(errorCode.getCode(),errorMsg,null);
    }

    private static<T> Response<T> buildResult(BaseEnum errorCode, T data) {
        return buildResult(errorCode.getCode(), errorCode.getMsg(), data);
    }

    private static<T> Response<T> buildResult(int errorCode, String errorMsg, T data) {
        return new Response(errorCode, errorMsg, data);
    }

}
