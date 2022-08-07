package com.wang.fastfood.apicommons.entity.DTO;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/4/27 16:08
 * @Description:
 */

@Data
public class StoreSalesInfoDTO {

    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 门店ID
     */
    private String storeId;
    /**
     * 销售额
     */
    private Double sales;
    /**
     * 微信支付数量
     */
    private Integer wxPayCount;
    /**
     * 账户支付数量
     */
    private Integer fastFoodPayCount;
    /**
     * 自取数量
     */
    private Integer pickUpCount;
    /**
     * 外送数量
     */
    private Integer deliveryCount;
    /**
     * 数据是否有效
     */
    private Integer valid;
    /**
     *
     */
    private String DataTime;

}
