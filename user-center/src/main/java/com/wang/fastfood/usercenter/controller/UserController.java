package com.wang.fastfood.usercenter.controller;

import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.usercenter.entity.BO.User;
import com.wang.fastfood.usercenter.exception.UserAlreadyExistException;
import com.wang.fastfood.usercenter.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 11:58
 * @Description:
 */

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/getByUserName")
    public UserDTO getByUserName(String userName){
        User user = userService.getByUserName(userName);
        return user.doBackward();
    }

    @PostMapping("/register")
    public Response<Integer> register(@RequestBody UserDTO userDTO) throws UserAlreadyExistException {
        User user = buildBO(userDTO);
        Integer result = userService.add(user);
        return ResponseUtil.success(result);
    }

    private User buildBO(UserDTO userDTO) {
        return BOUtils.convert(User.class, userDTO);
    }
}

