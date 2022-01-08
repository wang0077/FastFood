package com.wang.authcenter.controller;

import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 19:02
 * @Description:
 */

@RestController
public class LoginController {

    @PostMapping("/Test")
    public Response<String> login(@RequestBody UserDTO userDTO){
        System.out.println("????");
        return ResponseUtil.success("Login Success!");
    }

}
