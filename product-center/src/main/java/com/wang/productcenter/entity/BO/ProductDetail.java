package com.wang.productcenter.entity.BO;

import com.wang.fastfood.apicommons.entity.DTO.ProductDetailDTO;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import com.wang.fastfood.apicommons.entity.common.convert.POConvert;
import com.wang.productcenter.entity.PO.ProductDetailPO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 00:15
 * @Description:
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDetail extends Page implements Cloneable {

    /**
     * 商品详情ID
     */
    private Integer id;

    /**
     * 商品详情名称
     */
    private String productDetailName;

    /**
     * 可选种类列表
     */
    private List<DetailType> detailTypeList;

    /**
     * 是否获取子节点
     */
    private boolean isDetail;

    /**
     *  这是一个浅拷贝的clone!!!切记不要乱用!!!
     */
    @Override
    public ProductDetail clone() {
        ProductDetail productDetail = null;
        try {
            productDetail = (ProductDetail) super.clone();
        }catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return productDetail;
    }

    public ProductDetailPO doForward(){
        productDetailPOConvert convert = new productDetailPOConvert();
        return convert.convert(this);
    }

    public ProductDetailDTO doBackward(){
        ProductDetailDTOConvert convert = new ProductDetailDTOConvert();
        return convert.convert(this);
    }


    private static class productDetailPOConvert implements POConvert<ProductDetail, ProductDetailPO> {

        @Override
        public ProductDetailPO convert(ProductDetail productDetail) {
            ProductDetailPO productDetailPO = new ProductDetailPO();
            BeanUtils.copyProperties(productDetail,productDetailPO);
            return productDetailPO;
        }
    }

    private static class ProductDetailDTOConvert implements DTOConvert<ProductDetail, ProductDetailDTO> {

        @Override
        public ProductDetailDTO convert(ProductDetail productDetail) {
            ProductDetailDTO productDetailDTO = new ProductDetailDTO();
            BeanUtils.copyProperties(productDetail,productDetailDTO);
            if(productDetail.getDetailTypeList() != null){
                productDetailDTO.setDetailTypeDTOList(productDetail.getDetailTypeList()
                        .stream()
                        .map(DetailType::doBackward)
                        .collect(Collectors.toList()));
            }
            return productDetailDTO;
        }
    }
}
