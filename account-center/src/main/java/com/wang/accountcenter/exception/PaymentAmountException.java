package com.wang.accountcenter.exception;

import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.exception.FastFoodException;

/**
 * @Auther: wAnG
 * @Date: 2022/4/23 18:55
 * @Description:
 */

public class PaymentAmountException extends FastFoodException {

    public PaymentAmountException(String msg){
        super(msg,CodeEnum.PAY_AMOUNT_ERROR);
    }

}
