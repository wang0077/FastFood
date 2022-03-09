package com.wang.storeCenter.controller;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.Util.SqlResultUtil;
import com.wang.fastfood.apicommons.entity.DTO.StoreDTO;
import com.wang.fastfood.apicommons.entity.DTO.StoreRadiusDTO;
import com.wang.fastfood.apicommons.entity.DTO.UserCoordinateDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.storeCenter.entity.BO.Store;
import com.wang.storeCenter.entity.BO.StoreRadius;
import com.wang.storeCenter.service.IStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.GeoCoordinate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/1/8 17:19
 * @Description:
 */

@RestController
@RequestMapping("/store")
@CrossOrigin
public class storeController {

    @Autowired
    private IStoreService storeService;

    @PostMapping("/update")
    public Response<String> update(@RequestBody StoreDTO storeDTO) {
        Store store = buildBO(storeDTO);
        int result = storeService.update(store);
        return SqlResultUtil.updateResult(result);
    }

    @PostMapping("/delete")
    public Response<String> delete(@RequestBody StoreDTO storeDTO) {
        Store store = buildBO(storeDTO);
        int result = storeService.remove(store);
        return SqlResultUtil.deleteResult(result);
    }

    @PostMapping("/getAll")
    public Response<PageInfo<StoreDTO>> getAll(@RequestBody StoreDTO storeDTO) {
        Store store = buildBO(storeDTO);
        PageInfo<Store> result = storeService.getAll(store);

        return ResponseUtil.success(PageUtils.getPageInfo(result, result.getList()
                .stream()
                .map(Store::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/insert")
    public Response<String> insert(@RequestBody StoreDTO storeDTO) {
        Store store = buildBO(storeDTO);
        int result = storeService.insert(store);
        return SqlResultUtil.insertResult(result);
    }

    @PostMapping("/getById")
    public Response<StoreDTO> getById(@RequestBody StoreDTO storeDTO) {
        Store store = buildBO(storeDTO);
        Store result = storeService.getById(store);
        return ResponseUtil.success(result == null ? null : result.doBackward());
    }

    @PostMapping("/getByName")
    public Response<StoreDTO> getByName(@RequestBody StoreDTO storeDTO){
        Store store = buildBO(storeDTO);
        Store result = storeService.getByName(store);
        return ResponseUtil.success(result == null ? null : result.doBackward());
    }

    @PostMapping("/getLikeName")
    public Response<PageInfo<StoreDTO>> getLikeName(@RequestBody StoreDTO storeDTO) {
        Store store = buildBO(storeDTO);
        PageInfo<Store> result = storeService.getLikeName(store);
        return ResponseUtil.success(PageUtils.getPageInfo(result, result.getList()
                .stream().map(Store::doBackward)
                .collect(Collectors.toList())));
    }

    @PostMapping("/storeRadius")
    public Response<List<StoreRadiusDTO>> storeRadius(@RequestBody UserCoordinateDTO userCoordinateDTO){
        GeoCoordinate geoCoordinate = buildGeoCoordinate(userCoordinateDTO);
        double radius = userCoordinateDTO.getRadius();
        List<StoreRadius> storeList = storeService.storeRadius(geoCoordinate, radius);
        return ResponseUtil.success(storeList
                .stream()
                .map(StoreRadius::doBackward)
                .collect(Collectors.toList()));
    }

    private GeoCoordinate buildGeoCoordinate(UserCoordinateDTO userCoordinateDTO){
        return new GeoCoordinate(userCoordinateDTO.getLongitude(),userCoordinateDTO.getLatitude());
    }

    private Store buildBO(StoreDTO storeDTO) {
        return BOUtils.convert(Store.class, storeDTO);
    }

}
