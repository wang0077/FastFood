package com.wang.fastfood.apicommons.entity.common;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 21:19
 * @Description:
 */
public interface BOConvert <S,T>{
    public T convert(S s);
}
