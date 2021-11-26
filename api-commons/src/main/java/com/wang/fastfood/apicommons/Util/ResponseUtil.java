package com.wang.fastfood.apicommons.Util;

import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.BaseEnum;
import com.wang.fastfood.apicommons.enums.ErrorCodeEnum;


/**
 * @Auther: wAnG
 * @Date: 2021/11/23 21:29
 * @Description:
 */
@SuppressWarnings("all")
public class ResponseUtil {

    public static Response success() {
        return buildResult(ErrorCodeEnum.SUCCESS, null);
    }

    public static<T> Response<T> success(T data) {
        return buildResult(ErrorCodeEnum.SUCCESS, data);
    }

    public static<T> Response<T> success(BaseEnum errorCode, T data) {
        return buildResult(errorCode, data);
    }

    public static<T> Response<T> fail(BaseEnum errorCode) {
        return buildResult(errorCode, null);
    }

    public static<T> Response<T> fail(int errorCode, String errorMsg) {
        return buildResult(errorCode, errorMsg, null);
    }

    private static<T> Response<T> buildResult(BaseEnum errorCode, T data) {
        return buildResult(errorCode.getCode(), errorCode.getMsg(), data);
    }

    private static<T> Response<T> buildResult(int errorCode, String errorMsg, T data) {
        return new Response(errorCode, errorMsg, data);
    }
}
