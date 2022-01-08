package com.wang.fastfoodapi.controller;

import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfoodapi.entity.Request.UserRegisterRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: wAnG
 * @Date: 2022/1/3 17:45
 * @Description:
 */

@RestController
@CrossOrigin
@RequestMapping("user/")
public class RegisterController {

    @PostMapping("/register")
    public Response<String> register(@Validated @RequestBody UserRegisterRequest request){
        System.out.println(request);
        return null;
    }
}
