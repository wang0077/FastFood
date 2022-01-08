package com.wang.productcenter.exception;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.enums.ErrorCodeEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 01:18
 * @Description:
 */


//@ControllerAdvice
public class GlobalException {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Response<String> exceptionHandler(Exception e){
        return ResponseUtil.fail(ErrorCodeEnum.SERVER_ERROR,e.getMessage());
    }

}
