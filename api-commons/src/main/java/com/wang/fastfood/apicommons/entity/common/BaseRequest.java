package com.wang.fastfood.apicommons.entity.common;

import java.util.Date;

/**
 * @Auther: wAnG
 * @Date: 2021/11/23 21:28
 * @Description:
 */

public abstract class BaseRequest extends Page implements Validity{

    /**
     * IP
     */
    private String ip;

    /**
     * 请求参数
     */
    private String param;

    /**
     * 请求接口
     */
    private String uri;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 请求耗时（毫秒）
     */
    private Integer times;

    /**
     * 访问时间
     */
    private Date createTime;

}
