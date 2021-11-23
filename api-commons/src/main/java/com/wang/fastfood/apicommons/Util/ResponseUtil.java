package com.wang.fastfood.apicommons.Util;

import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.BaseEnum;
import com.wang.fastfood.apicommons.enums.ErrorCodeEnum;

/**
 * @Auther: wAnG
 * @Date: 2021/11/23 21:29
 * @Description:
 */

public class ResponseUtil {
    public static Response success() {
        return buildResult(ErrorCodeEnum.SUCCESS, null);
    }

    public static Response success(Object data) {
        return buildResult(ErrorCodeEnum.SUCCESS, data);
    }

    public static Response success(BaseEnum errorCode, Object data) {
        return buildResult(errorCode, data);
    }

    public static Response fail(BaseEnum errorCode) {
        return buildResult(errorCode, null);
    }

    public static Response fail(int errorCode, String errorMsg) {
        return buildResult(errorCode, errorMsg, null);
    }

    private static Response buildResult(BaseEnum errorCode, Object data) {
        return buildResult(errorCode.getCode(), errorCode.getMsg(), data);
    }

    private static Response buildResult(int errorCode, String errorMsg, Object data) {
        return new Response(errorCode, errorMsg, data);
    }
}
