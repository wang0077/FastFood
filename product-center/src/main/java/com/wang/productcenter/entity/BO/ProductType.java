package com.wang.productcenter.entity.BO;

import com.wang.productcenter.entity.PO.ProductTypePO;
import com.wang.productcenter.common.POConvert;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 21:14
 * @Description:
 */

@Data
public class ProductType {
    /**
     * 种类ID
     */
    private Integer id;
    /**
     * 种类名称
     */
    private String name;

    public ProductTypePO convertToProductTypePO(){
        ProductTypePOConvert convert = new ProductTypePOConvert();
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
}
