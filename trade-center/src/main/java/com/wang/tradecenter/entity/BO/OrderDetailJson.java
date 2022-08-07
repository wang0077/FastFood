package com.wang.tradecenter.entity.BO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/25 18:50
 * @Description:
 */

@Data
public class OrderDetailJson {
    private String productName;

    private List<String> productDetailName = new ArrayList<>();

    private int number;

    private double price = 0;

    public void setPrice(double price){
        this.price += price;
    }

    public void addProductDetailName(String detailTypeName){
        this.productDetailName.add(detailTypeName);
    }

}
