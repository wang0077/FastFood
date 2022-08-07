package com.wang.fastfood.apicommons.entity.DTO;

import com.wang.fastfood.apicommons.entity.common.Page;
import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/4/25 15:26
 * @Description:
 */

@Data
public class ExceptionOrderDTO extends Page {

    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 门店ID
     */
    private Integer storeId;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 电话号码
     */
    private String phoneNumber;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 商品订单详情（JSON格式）
     */
    private String OrderDetailJSON;
    /**
     * 取餐方式
     */
    private Integer takeMethod;
    /**
     * 取餐方式名称
     */
    private String takeMethodName;
    /**
     * 订单金额
     */
    private Double orderAmount;
    /**
     * 下单时间
     */
    private String orderTime;
    /**
     * 异常信息
     */
    private String exceptionInfo;
    /**
     * 取单号
     */
    private Integer takeOrderNumber;

}
