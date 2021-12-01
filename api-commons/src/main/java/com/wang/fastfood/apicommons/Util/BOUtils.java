package com.wang.fastfood.apicommons.Util;

import org.springframework.beans.BeanUtils;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 03:22
 * @Description:
 */

public class BOUtils {

    public static <S,T> T convert(Class<T> clazz, S s){
        try {
            T t = clazz.newInstance();
            BeanUtils.copyProperties(s,t);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
