package com.wang.tradecenter.fegin;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.entity.DTO.StoreDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/23 15:08
 * @Description:
 */


@Service
@FeignClient(name = "Store-Center")
public interface StoreClient {

    @PostMapping("/store/getAll")
    Response<PageInfo<StoreDTO>> getStoreAll(StoreDTO storeDTO);

    @PostMapping("/store/getByIds")
    Response<List<StoreDTO>> getByIds(@RequestBody List<Integer> storeIds);

}
