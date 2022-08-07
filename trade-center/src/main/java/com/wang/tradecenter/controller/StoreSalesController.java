package com.wang.tradecenter.controller;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.StoreSalesInfoDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.tradecenter.entity.BO.StoreSalesInfo;
import com.wang.tradecenter.service.IStoreSalesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/4/27 21:03
 * @Description:
 */

@RestController
@RequestMapping("/order")
public class StoreSalesController {

    @Autowired
    private IStoreSalesInfoService storeSalesInfoService;

    @PostMapping("/storeSalesInfoSevenDay")
    public Response<List<StoreSalesInfoDTO>> storeSalesInfoSevenDay(@RequestBody StoreSalesInfoDTO storeSalesInfoDTO){
        String storeId = storeSalesInfoDTO.getStoreId();
        List<StoreSalesInfo> storeSalesInfos = storeSalesInfoService.storeSalesInfoSevenDay(storeId);
        return ResponseUtil.success(storeSalesInfos.stream()
                .map(StoreSalesInfo::doBackward)
                .collect(Collectors.toList()));
    }

    @PostMapping("/adminStoreSalesInfoDays")
    public Response<List<StoreSalesInfoDTO>> adminStoreSalesInfoDays(){
        List<StoreSalesInfo> storeSalesInfos = storeSalesInfoService.adminStoreSalesInfoDays();
        return ResponseUtil.success(storeSalesInfos.stream()
                .map(StoreSalesInfo::doBackward)
                .collect(Collectors.toList()));
    }



}
