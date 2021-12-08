package com.wang.fastfood.apicommons.entity.common;

import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2021/12/9 02:09
 * @Description:
 */

@Data
public abstract class Page {

    private Integer pageNum;

    private Integer pageSize;

    private String orderBy;

    public boolean IsPage(){
        return pageNum != null && pageSize != null;
    }

}
