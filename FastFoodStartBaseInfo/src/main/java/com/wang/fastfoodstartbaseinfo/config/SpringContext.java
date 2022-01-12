package com.wang.fastfoodstartbaseinfo.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Auther: wAnG
 * @Date: 2021/11/27 03:00
 * @Description:
 */

@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.context = applicationContext;
    }

    public static Object getBean(String name){
        return context.getBean(name);
    }

    public static<T> T getBean(Class<T> Class){
        return context.getBean(Class);
    }
}
