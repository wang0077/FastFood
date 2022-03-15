package com.wang.productcenter.exception;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.exception.FastFoodException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 01:18
 * @Description:
 */


@ControllerAdvice
public class GlobalException {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Response<String> exceptionHandler(Exception e){
        if(e instanceof FastFoodException){
            FastFoodException exception = (FastFoodException) e;
            return ResponseUtil.fail(exception.codeEnum,e.getMessage());
        }
        return ResponseUtil.fail(CodeEnum.SERVER_ERROR,e.getMessage());
    }

}
