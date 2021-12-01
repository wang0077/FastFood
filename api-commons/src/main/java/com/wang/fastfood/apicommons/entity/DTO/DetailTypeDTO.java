package com.wang.fastfood.apicommons.entity.DTO;

import com.google.common.base.Strings;
import com.wang.fastfood.apicommons.entity.common.BaseRequest;
import com.wang.fastfood.apicommons.exception.ParamException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 02:45
 * @Description:
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class DetailTypeDTO extends BaseRequest {

    /**
     * 商品详情分类ID
     */
    private Integer id;
    /**
     * 商品详情分类名称
     */
    private String detailTypeName;

    @Override
    public void validity(){
        try {
            if(Strings.isNullOrEmpty(detailTypeName)){
                throw new ParamException("参数detailTypeName为空");
            }
        }catch (ParamException e){
            e.printStackTrace();
        }
    }
}
