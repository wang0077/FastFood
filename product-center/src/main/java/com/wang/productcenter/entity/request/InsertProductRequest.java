package com.wang.productcenter.entity.request;

import com.wang.fastfood.apicommons.entity.DTO.ProductDetailDTO;
import com.wang.fastfood.apicommons.entity.common.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/9 17:49
 * @Description:
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class InsertProductRequest extends BaseRequest {

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
     * 商品描述
     */
    private String describe;

    /**
     * 商品详情可选项
     */
    private List<ProductDetailDTO> productDetailDTOList;

}
