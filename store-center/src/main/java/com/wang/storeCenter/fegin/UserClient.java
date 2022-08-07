package com.wang.storeCenter.fegin;

import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.entity.request.addAdminRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Auther: wAnG
 * @Date: 2022/4/24 15:20
 * @Description:
 */

@Service
@FeignClient(name = "User-Center")
public interface UserClient {

    @PostMapping("/user/adminRegister")
    Response<Integer> addAdmin(addAdminRequest request);

}
