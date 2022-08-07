package com.wang.tradecenter.entity.BO;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/4/10 02:08
 * @Description:
 */

@Data
public class HistoryOrder {
    /**
     * 订单商品详情（JSON格式存储）
     */
    private String orderDetailJson;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 订单状态名称
     */
    private String orderStatusName;
    /**
     * 订单金额
     */
    private Double orderAmount;
    /**
     * 取单号
     */
    private Integer takeOrderNumber;
    /**
     * 门店ID
     */
    private String storeId;
    /**
     * 完成制作时间
     */
    private String completeProductionTime;
    /**
     * 下单时间
     */
    private String orderTime;
    /**
     * 门店名字
     */
    private String StoreName;
    /**
     * 门店地址
     */
    private String storeAddress;

}
