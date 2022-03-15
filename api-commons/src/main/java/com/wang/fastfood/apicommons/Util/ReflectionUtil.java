package com.wang.fastfood.apicommons.Util;

import java.lang.reflect.Field;

/**
 * @Auther: wAnG
 * @Date: 2022/3/13 00:05
 * @Description:
 */

public class ReflectionUtil {

    public static <T> Object getFieldValue(Class<T> clazz,String FieldName,Object target){
        Object result = null;
        try {
            Field field = clazz.getDeclaredField(FieldName);
            field.setAccessible(true);
            result = field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

}
