package com.wang.tradecenter.entity.PO;

import com.wang.fastfood.apicommons.entity.common.convert.BOConvert;
import com.wang.tradecenter.entity.BO.StoreSalesInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.ZoneId;
import java.util.Date;

/**
 * @Auther: wAnG
 * @Date: 2022/4/27 15:42
 * @Description:
 */

@Data
public class StoreSalesInfoPO {

    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 门店ID
     */
    private String storeId;
    /**
     * 销售额
     */
    private Double sales;
    /**
     * 微信支付数量
     */
    private Integer wxPayCount;
    /**
     * 账户支付数量
     */
    private Integer fastFoodPayCount;
    /**
     * 自取数量
     */
    private Integer pickUpCount;
    /**
     * 外送数量
     */
    private Integer deliveryCount;
    /**
     * 数据是否有效
     */
    private Integer valid;
    /**
     *
     */
    private Date crateTime;
    /**
     *
     */
    private Date updateTime;

    public StoreSalesInfo convertToStoreSalesInfo(){
        storeSalesInfoBOConvert convert = new storeSalesInfoBOConvert();
        return convert.convert(this);
    }

    private static class storeSalesInfoBOConvert implements BOConvert<StoreSalesInfoPO, StoreSalesInfo> {

        @Override
        public StoreSalesInfo convert(StoreSalesInfoPO storeSalesInfoPO) {
            StoreSalesInfo storeSalesInfo = new StoreSalesInfo();
            if(storeSalesInfoPO.crateTime != null){
                storeSalesInfo.setDataTime(storeSalesInfoPO.getCrateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            BeanUtils.copyProperties(storeSalesInfoPO,storeSalesInfo);
            return storeSalesInfo;
        }
    }

}
