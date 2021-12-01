package com.wang.productcenter.controller;

import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.DetailTypeDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.service.IDetailTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 02:30
 * @Description:
 */

@RestController
@RequestMapping("/detailType")
public class DetailTypeController {

    @Autowired
    IDetailTypeService detailTypeService;

    @GetMapping("/getAll")
    public Response<List<DetailTypeDTO>> getAll(){
        List<DetailType> result = detailTypeService.getAll();
        return ResponseUtil.success(result.stream().map(DetailType::doBackward).collect(Collectors.toList()));
    }

    @PostMapping("/getById")
    public Response<DetailTypeDTO> getById(DetailTypeDTO detailTypeDTO){
        DetailType detailType = BOUtils.convert(DetailType.class, detailTypeDTO);
        DetailType result = detailTypeService.getById(detailType);
        return ResponseUtil.success(result.doBackward());
    }

    @PostMapping("/getByName")
    public Response<List<DetailTypeDTO>> getByName(DetailTypeDTO detailTypeDTO){
        DetailType detailType = BOUtils.convert(DetailType.class, detailTypeDTO);
        List<DetailType> result = detailTypeService.getByName(detailType);
        return ResponseUtil.success(result.stream().map(DetailType::doBackward).collect(Collectors.toList()));
    }

    @PostMapping("/delete")
    public Response remove(DetailTypeDTO detailTypeDTO){
        DetailType detailType = BOUtils.convert(DetailType.class, detailTypeDTO);
        detailTypeService.remove(detailType);
        return ResponseUtil.success();
    }

    @PostMapping("/update")
    public Response update(DetailTypeDTO detailTypeDTO){
        DetailType detailType = BOUtils.convert(DetailType.class, detailTypeDTO);
        detailTypeService.update(detailType);
        return ResponseUtil.success();
    }

}
