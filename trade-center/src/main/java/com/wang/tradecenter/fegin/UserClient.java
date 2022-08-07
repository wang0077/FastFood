package com.wang.tradecenter.fegin;

import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.entity.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/26 17:27
 * @Description:
 */

@Service
@FeignClient(name = "User-Center")
public interface UserClient {

    @PostMapping("/user/getByUserIds")
    Response<List<UserInfoResponse>> getUserByUidList(List<String> uidList);

}
