package com.wang.authcenter.service.Remote;

import com.wang.authcenter.config.FeignConfig;
import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping(method = RequestMethod.POST, value = "/user/getByUserId")
    Response<UserDTO> getById(@RequestParam("uid") String uid);

    @RequestMapping(method = RequestMethod.POST, value = "user/register")
    void UserRegister(@RequestBody UserDTO userDTO);

}
