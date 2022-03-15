package com.wang.fastfood.apicommons.entity.common.convert;

/**
 * @Auther: wAnG
 * @Date: 2021/12/1 22:31
 * @Description:
 */
public interface POConvert <S,T> {
    T convert(S s);
}
