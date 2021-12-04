package com.wang.productcenter.entity.BO;

import com.wang.fastfood.apicommons.entity.DTO.ProductDTO;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import com.wang.productcenter.entity.PO.ProductPO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 21:01
 * @Description:
 */

@Data
public class Product {
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
    private List<ProductDetail> productDetailList;

    /**
     * 商品分类
     */
    private ProductType productType;

    public ProductPO doForward(){
        ProductPOConvert convert = new ProductPOConvert();
        return convert.convert(this);
    }

    public ProductDTO doBackward(){
        ProductDTOConvert convert = new ProductDTOConvert();
        return convert.convert(this);
    }


    private static class ProductPOConvert implements POConvert<Product, ProductPO> {

        @Override
        public ProductPO convert(Product product) {
            ProductPO productPO = new ProductPO();
            BeanUtils.copyProperties(product,productPO);
            return productPO;
        }
    }

    private static class ProductDTOConvert implements DTOConvert<Product, ProductDTO> {

        @Override
        public ProductDTO convert(Product product) {
            ProductDTO productDTO = new ProductDTO();
            if(product.productDetailList != null){
                productDTO.setProductDetailDTOList(product.productDetailList
                        .stream()
                        .map(ProductDetail::doBackward)
                        .collect(Collectors.toList()));
            }
            if(product.productType != null){
                productDTO.setProductTypeDTO(product.productType.doBackward());
            }
            BeanUtils.copyProperties(product,productDTO);
            return productDTO;
        }
    }
}
