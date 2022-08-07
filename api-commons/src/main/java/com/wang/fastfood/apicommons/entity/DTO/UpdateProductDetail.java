package com.wang.fastfood.apicommons.entity.DTO;

import lombok.Data;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/23 18:28
 * @Description:
 */

@Data
public class UpdateProductDetail {

    private String productName;

    private Integer number;

    private List<String> productDetailName;

    private Double price;

}
