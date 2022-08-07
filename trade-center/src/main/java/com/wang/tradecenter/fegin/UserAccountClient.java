package com.wang.tradecenter.fegin;

import com.wang.fastfood.apicommons.entity.request.CalculateInterestRequest;
import com.wang.fastfood.apicommons.entity.request.PayRequest;
import com.wang.fastfood.apicommons.entity.common.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Auther: wAnG
 * @Date: 2022/4/23 19:01
 * @Description:
 */

@Service
@FeignClient(name = "Account-Center")
public interface UserAccountClient {

    @PostMapping("/Account/pay")
    Response pay(PayRequest payRequest);

    @PostMapping("/Account/calculate")
    Response<String> calculateOrderInterest(@RequestBody CalculateInterestRequest request);

}
