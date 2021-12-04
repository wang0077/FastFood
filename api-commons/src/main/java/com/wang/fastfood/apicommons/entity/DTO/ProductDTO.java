package com.wang.fastfood.apicommons.entity.DTO;

import lombok.Data;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 21:08
 * @Description:
 */

@Data
public class ProductDTO {

    /**
     * 商品ID
     */
    private Integer id;

    /**
     * 种类ID
     */
    private Integer typeId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格
     */
    private Double productPrice;

    /**
     * 商品图片
     */
    private String productImagePath;
    /**
     * 商品销量
     */
    private Integer sales;

    /**
     * 商品是否上架
     */
    private Integer isSales;

    /**
     * 商品详情可选项
     */
    private List<ProductDetailDTO> productDetailDTOList;

    private ProductTypeDTO productTypeDTO;

}
