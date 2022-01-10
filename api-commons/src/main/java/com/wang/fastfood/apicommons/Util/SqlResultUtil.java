package com.wang.fastfood.apicommons.Util;

import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.CodeEnum;

/**
 * @Auther: wAnG
 * @Date: 2021/12/3 00:46
 * @Description:
 */

@SuppressWarnings("all")
public class SqlResultUtil {

    public static Response<String> insertResult(int result) {
        if (result > 0) {
            return ResponseUtil.success(CodeEnum.SQL_INSERT_SUCCESS);
        } else if (result == -1) {
            return ResponseUtil.fail(CodeEnum.SQL_REPEAT_INSERT_ERROR);
        } else {
            return ResponseUtil.fail(CodeEnum.SQL_INSERT_ERROR);
        }
    }

    public static Response<String> updateResult(int result){
        if(result > 0){
            return ResponseUtil.success(CodeEnum.SQL_UPDATE_SUCCESS);
        }else {
            return ResponseUtil.fail(CodeEnum.SQL_UPDATE_ERROR);
        }
    }

    public static Response<String> deleteResult(int result){
        if(result > 0){
            return ResponseUtil.success(CodeEnum.SQL_DELETE_SUCCESS);
        }else {
            return ResponseUtil.fail(CodeEnum.SQL_DELETE_ERROR);
        }
    }

}
