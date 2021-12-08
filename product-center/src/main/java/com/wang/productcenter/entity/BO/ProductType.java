package com.wang.productcenter.entity.BO;

import com.wang.fastfood.apicommons.entity.DTO.ProductTypeDTO;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.productcenter.entity.PO.ProductTypePO;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 21:14
 * @Description:
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductType extends Page {
    /**
     * 种类ID
     */
    private Integer id;
    /**
     * 种类名称
     */
    private String name;

    public ProductTypePO doForward(){
        ProductTypePOConvert convert = new ProductTypePOConvert();
        return convert.convert(this);
    }

    public ProductTypeDTO doBackward(){
        ProductTypeDTOConvert convert = new ProductTypeDTOConvert();
        return convert.convert(this);
    }


    private static class ProductTypePOConvert implements POConvert<ProductType, ProductTypePO>{

        @Override
        public ProductTypePO convert(ProductType productType) {
            ProductTypePO productTypePO = new ProductTypePO();
            BeanUtils.copyProperties(productType,productTypePO);
            return productTypePO;
        }
    }

    private static class ProductTypeDTOConvert implements DTOConvert<ProductType, ProductTypeDTO>{

        @Override
        public ProductTypeDTO convert(ProductType productType) {
            ProductTypeDTO productTypeDTO = new ProductTypeDTO();
            BeanUtils.copyProperties(productType,productTypeDTO);
            return productTypeDTO;
        }
    }
}
