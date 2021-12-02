package com.wang.fastfood.apicommons.Util;

import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.ErrorCodeEnum;

/**
 * @Auther: wAnG
 * @Date: 2021/12/3 00:46
 * @Description:
 */

@SuppressWarnings("all")
public class SqlResultUtil {

    public static Response insertResult(int result) {
        if (result > 0) {
            return ResponseUtil.success(null);
        } else if (result == -1) {
            return ResponseUtil.fail(ErrorCodeEnum.SQL_REPEAT_INSERT_ERROR);
        } else {
            return ResponseUtil.fail(ErrorCodeEnum.SQL_INSERT_ERROR);
        }
    }

    public static Response updateResult(int result){
        if(result != 0){
            return ResponseUtil.success(null);
        }else {
            return ResponseUtil.fail(ErrorCodeEnum.SQL_INSERT_ERROR);
        }
    }

}
