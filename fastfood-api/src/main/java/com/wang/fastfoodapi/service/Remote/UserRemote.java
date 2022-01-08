package com.wang.fastfoodapi.service.Remote;

import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import com.wang.fastfoodapi.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 12:34
 * @Description:
 */

@FeignClient(value = "User-Center",configuration = FeignConfig.class)
@Service
public interface UserRemote {

    @RequestMapping(method = RequestMethod.POST,value = "user/getByUserName")
    UserDTO getByName(@RequestParam("userName") String userName);

}
