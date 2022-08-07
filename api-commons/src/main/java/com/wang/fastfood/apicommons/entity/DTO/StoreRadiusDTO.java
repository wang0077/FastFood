package com.wang.fastfood.apicommons.entity.DTO;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/1/10 21:25
 * @Description:
 */

@Data
public class StoreRadiusDTO {

    private StoreDTO store;

    private double distance;

    private boolean business;

}
