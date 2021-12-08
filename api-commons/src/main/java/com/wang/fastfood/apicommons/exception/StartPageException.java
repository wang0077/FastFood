package com.wang.fastfood.apicommons.exception;

/**
 * @Auther: wAnG
 * @Date: 2021/12/9 02:24
 * @Description:
 */

public class StartPageException extends Exception{
    public StartPageException(){
        super("该类型Bean无法进行分页");
    }
}
