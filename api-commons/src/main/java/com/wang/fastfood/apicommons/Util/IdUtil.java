package com.wang.fastfood.apicommons.Util;

import java.util.Locale;
import java.util.UUID;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 16:45
 * @Description:
 */

public class IdUtil {

    public static String getID(){
       return UUID.randomUUID().toString().replaceAll("-","").toUpperCase(Locale.ROOT);
    }

}
