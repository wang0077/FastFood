package com.wang.accountcenter.controller;

import com.wang.accountcenter.entity.BO.SignIn;
import com.wang.accountcenter.service.ISignInService;
import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.SignInDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 21:21
 * @Description:
 */

@Controller
@RequestMapping("/SignIn")
public class SignInController {

    @Autowired
    private ISignInService signInService;

    @PostMapping("/checkIn")
    public Response<SignInDTO> checkIn(@RequestBody SignInDTO signInDTO){
        SignIn sign = buildBO(signInDTO);
        SignIn result = signInService.checkIn(sign);
        return ResponseUtil.success(result.doBackward());
    }

    private SignIn buildBO(SignInDTO signInDTO) {
        return BOUtils.convert(SignIn.class, signInDTO);
    }

}
