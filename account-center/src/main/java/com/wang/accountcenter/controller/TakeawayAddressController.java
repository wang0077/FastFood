package com.wang.accountcenter.controller;

import com.wang.accountcenter.entity.BO.TakeawayAddress;
import com.wang.accountcenter.service.ITakeawayAddressService;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.TakeawayAddressDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/4/12 02:35
 * @Description:
 */

@RestController
@RequestMapping("/address")
public class TakeawayAddressController {

    @Autowired
    private ITakeawayAddressService takeawayAddressService;

    @PostMapping("/insert")
    public Response insert(@RequestBody TakeawayAddressDTO takeawayAddressDTO){
        TakeawayAddress takeawayAddress = buildBO(takeawayAddressDTO);
        takeawayAddressService.insert(takeawayAddress);
        return ResponseUtil.success();
    }

    @PostMapping("/getByUid")
    public Response<List<TakeawayAddressDTO>> getByUid(@RequestBody TakeawayAddressDTO takeawayAddressDTO){
        String openId = takeawayAddressDTO.getUid();
        List<TakeawayAddress> result = takeawayAddressService.getByUid(openId);
        return ResponseUtil.success(result.stream()
                .map(TakeawayAddress::doBackward)
                .collect(Collectors.toList()));
    }

    private TakeawayAddress buildBO(TakeawayAddressDTO takeawayAddressDTO) {
        return BOUtils.convert(TakeawayAddress.class, takeawayAddressDTO);
    }

}
