package com.wang.fastfood.apicommons.entity.DTO;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/1/10 20:27
 * @Description:
 */

@Data
public class UserCoordinateDTO {

    private Integer id;

    private double longitude;

    private double latitude;

    private double radius;
}
