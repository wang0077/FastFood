package com.wang.fastfood.apicommons.entity.common.convert;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 21:19
 * @Description:
 */
public interface BOConvert <S,T> {
    T convert(S s);
}
