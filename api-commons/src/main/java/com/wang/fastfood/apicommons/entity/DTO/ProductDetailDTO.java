package com.wang.fastfood.apicommons.entity.DTO;

import com.google.common.base.Strings;
import com.wang.fastfood.apicommons.entity.common.BaseRequest;
import com.wang.fastfood.apicommons.exception.ParamException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 00:52
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductDetailDTO extends BaseRequest {
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
    private List<DetailTypeDTO> detailTypeDTOList;

    /**
     * 是否获取子节点
     */
    private boolean isDetail;

    public boolean isDetail() {
        return isDetail;
    }

    public void setIsDetail(boolean detail) {
        isDetail = detail;
    }

    @Override
    public void validity() {
        try {
            if(Strings.isNullOrEmpty(productDetailName)){
                throw new ParamException("参数productDetailName为空");
            }
        }catch (ParamException e){
            e.printStackTrace();
        }
    }
}
