package com.wang.fastfood.apicommons.entity.common.convert;

/**
 * @Auther: wAnG
 * @Date: 2021/11/23 22:09
 * @Description:
 */
public interface DTOConvert<S,T> {
    T convert(S s);
}
