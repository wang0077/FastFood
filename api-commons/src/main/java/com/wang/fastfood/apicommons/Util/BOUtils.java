package com.wang.fastfood.apicommons.Util;

import org.springframework.beans.BeanUtils;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 03:22
 * @Description:
 */

public class BOUtils {

    // todo BeanUtil是浅拷贝，存在无法深拷贝风险
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
