package com.wang.productcenter.controller;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.Util.SqlResultUtil;
import com.wang.fastfood.apicommons.entity.DTO.DetailTypeDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.service.IDetailTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 02:30
 * @Description:
 */

@RestController
@CrossOrigin
@RequestMapping("/detailType")
@SuppressWarnings("all")
public class DetailTypeController {

    @Autowired
    IDetailTypeService detailTypeService;

    @PostMapping("/flush")
    public Response flushZset(){
        detailTypeService.flush();
        return ResponseUtil.success();
    }

    @PostMapping("/getAll")
    public Response<PageInfo<DetailTypeDTO>> getAll(@RequestBody DetailTypeDTO detailTypeDTO) {
        DetailType detailType = buildBO(detailTypeDTO);
        PageInfo<DetailType> result = detailTypeService.getAll(detailType);
        return ResponseUtil.success(PageUtils.getPageInfo(result, result.getList()
                .stream()
                .map(DetailType::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/insert")
    public Response insert(@RequestBody DetailTypeDTO detailTypeDTO) {
        DetailType detailType = buildBO(detailTypeDTO);
        int result = detailTypeService.insert(detailType);
        return SqlResultUtil.insertResult(result);
    }

    @PostMapping("/getById")
    public Response<DetailTypeDTO> getById(@RequestBody DetailTypeDTO detailTypeDTO) {
        DetailType detailType = buildBO(detailTypeDTO);
        DetailType result = detailTypeService.getById(detailType);
        return ResponseUtil.success(result.doBackward());
    }

    @PostMapping("/getByProductDetailId")
    public Response<PageInfo<DetailTypeDTO>> getByProductDetailId(@RequestBody DetailTypeDTO detailTypeDTO) {
        DetailType detailType = buildBO(detailTypeDTO);
        PageInfo<DetailType> result = detailTypeService.getByProductDetailId(detailType);
        return ResponseUtil.success(PageUtils.getPageInfo(result, result.getList()
                .stream()
                .map(DetailType::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/getByName")
    public Response<DetailTypeDTO> getByName(@RequestBody DetailTypeDTO detailTypeDTO) {
        DetailType detailType = buildBO(detailTypeDTO);
        DetailType result = detailTypeService.getByName(detailType);
        return ResponseUtil.success(result != null ? result.doBackward() : null);
    }

    @PostMapping("/getLikeName")
    public Response<PageInfo<DetailTypeDTO>> getLikeName(@RequestBody DetailTypeDTO detailTypeDTO) {
        DetailType detailType = buildBO(detailTypeDTO);
        PageInfo<DetailType> result = detailTypeService.getLikeName(detailType);
        return ResponseUtil.success(PageUtils.getPageInfo(result, result.getList()
                .stream()
                .map(DetailType::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/delete")
    public Response remove(@RequestBody DetailTypeDTO detailTypeDTO) {
        DetailType detailType = buildBO(detailTypeDTO);
        detailTypeService.remove(detailType);
        return ResponseUtil.success();
    }

    @PostMapping("/update")
    public Response update(@RequestBody DetailTypeDTO detailTypeDTO) {
        DetailType detailType = buildBO(detailTypeDTO);
        int result = detailTypeService.update(detailType);
        return SqlResultUtil.updateResult(result);
    }

    private DetailType buildBO(DetailTypeDTO detailTypeDTO) {
        return BOUtils.convert(DetailType.class, detailTypeDTO);
    }

}
