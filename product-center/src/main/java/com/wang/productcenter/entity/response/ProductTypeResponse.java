package com.wang.productcenter.entity.response;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 21:11
 * @Description:
 */

@Data
public class ProductTypeResponse {

    /**
     * 种类ID
     */
    private Integer id;
    /**
     * 种类名称
     */
    private String name;

}
