package com.wang.tradecenter.entity.request;

import com.wang.fastfood.apicommons.entity.common.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: wAnG
 * @Date: 2022/3/26 18:37
 * @Description:
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderInfoRequest extends Page {

    public String OrderId;

    public String storeId;

    public String selectStatue;

    public String uid;

}
