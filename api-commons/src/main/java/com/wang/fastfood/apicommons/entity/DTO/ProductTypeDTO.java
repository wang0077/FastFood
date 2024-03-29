package com.wang.fastfood.apicommons.entity.DTO;

import com.google.common.base.Strings;
import com.wang.fastfood.apicommons.entity.common.BaseRequest;
import com.wang.fastfood.apicommons.exception.ParamException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 00:52
 * @Description:
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductTypeDTO extends BaseRequest {

    /**
     * 种类ID
     */
    private Integer id;
    /**
     * 种类名称
     */
    private String name;

    @Override
    public void validity(){
        try {
            if(Strings.isNullOrEmpty(name)){
                throw new ParamException("参数name为空");
            }
        }catch (ParamException e){
            e.printStackTrace();
        }
    }
}
