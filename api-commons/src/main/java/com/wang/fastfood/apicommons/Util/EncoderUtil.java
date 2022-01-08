package com.wang.fastfood.apicommons.Util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Matcher;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 16:35
 * @Description:
 */

public class EncoderUtil {

    public static PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encoder(String password){
        return encoder.encode(password);
    }

    public static String replace(String password){
        return Matcher.quoteReplacement(password);
    }


}
