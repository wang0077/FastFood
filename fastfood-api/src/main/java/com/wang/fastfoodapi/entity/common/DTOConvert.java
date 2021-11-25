package com.wang.fastfoodapi.entity.common;

/**
 * @Auther: wAnG
 * @Date: 2021/11/23 22:09
 * @Description:
 */
public interface DTOConvert<S,T> {
    T convert(S s);
}
