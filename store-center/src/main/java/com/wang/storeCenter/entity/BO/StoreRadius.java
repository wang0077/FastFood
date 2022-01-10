package com.wang.storeCenter.entity.BO;

import com.wang.fastfood.apicommons.entity.DTO.StoreRadiusDTO;
import com.wang.fastfood.apicommons.entity.common.convert.DTOConvert;
import lombok.Data;

/**
 * @Auther: wAnG
 * @Date: 2022/1/10 20:43
 * @Description:
 */

@Data
public class StoreRadius {

    /**
     *  redis中的Key
     */
    private String member;

    private Store store;

    private double distance;

    public StoreRadiusDTO doBackward(){
        storeRadiusDTOConvert convert = new storeRadiusDTOConvert();
        return convert.convert(this);
    }

    private static class storeRadiusDTOConvert implements DTOConvert<StoreRadius, StoreRadiusDTO> {

        @Override
        public StoreRadiusDTO convert(StoreRadius storeRadius) {
            StoreRadiusDTO storeRadiusDTO  = new StoreRadiusDTO();
            storeRadiusDTO.setDistance(storeRadius.getDistance());
            storeRadiusDTO.setStore(storeRadius.getStore().doBackward());
            return storeRadiusDTO;
        }
    }

}
