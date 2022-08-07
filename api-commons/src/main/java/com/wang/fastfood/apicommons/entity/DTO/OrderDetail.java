package com.wang.fastfood.apicommons.entity.DTO;

import lombok.Data;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/21 21:39
 * @Description:
 */

@Data
public class OrderDetail {

    private Integer productId;

    private List<Integer> detailTypeIds;

    private int number;

}
