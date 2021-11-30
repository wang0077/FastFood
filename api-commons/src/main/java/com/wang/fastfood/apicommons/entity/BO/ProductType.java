package com.wang.fastfood.apicommons.entity.BO;

import com.wang.fastfood.apicommons.entity.PO.ProductTypePO;
import com.wang.fastfood.apicommons.entity.common.BOConvert;
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

}
