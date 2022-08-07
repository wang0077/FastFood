package com.wang.fastfood.usercenter.controller;

import com.wang.fastfood.apicommons.Util.BOUtils;
import com.wang.fastfood.apicommons.Util.ResponseUtil;
import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.entity.request.addAdminRequest;
import com.wang.fastfood.apicommons.entity.response.UserInfoResponse;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.usercenter.entity.BO.User;
import com.wang.fastfood.usercenter.entity.request.EditPasswordRequest;
import com.wang.fastfood.usercenter.entity.request.WXGetUserInfoRequest;
import com.wang.fastfood.usercenter.entity.request.WXSaveUserInfoRequest;
import com.wang.fastfood.usercenter.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    public UserDTO getByUserName(String userName) {
        User user = userService.getByUserName(userName);
        return user.doBackward();
    }

    @PostMapping("/getByUserId")
    public Response<UserDTO> getByUId(String uid) {
        User user = userService.getByUserId(uid);
        return user == null ? ResponseUtil.fail(CodeEnum.USER_NOT_EXIST) : ResponseUtil.success(user.doBackward());
    }

    @PostMapping("/getByUserIds")
    public Response<List<UserInfoResponse>> getByUIds(@RequestBody List<String> uidList) {
        List<User> result = userService.getByUserIds(uidList);
        return ResponseUtil.success(result.stream().map(item -> {
            UserInfoResponse response = new UserInfoResponse();
            BeanUtils.copyProperties(item, response);
            return response;
        }).collect(Collectors.toList()));
    }

    @PostMapping("/adminRegister")
    public Response<Integer> adminRegister(@RequestBody addAdminRequest request) {
        User user = buildUser(request);
        Integer result = userService.addStoreAdmin(user);
        return ResponseUtil.success(result);
    }

    @PostMapping("/register")
    public Response<Integer> register(@RequestBody UserDTO userDTO) {
        User user = buildBO(userDTO);
        Integer result = userService.add(user);
        return ResponseUtil.success(result);
    }

    @PostMapping("/getUserInfo")
    public Response<UserDTO> WXGetUserInfoByOpenId(@RequestBody WXGetUserInfoRequest request) {
        String openId = request.getOpenId();
        User user = userService.WXGetUserInfo(openId);
        return ResponseUtil.success(user.doBackward());
    }

    @PostMapping("/saveUserInfo")
    public Response<Void> WXSaveUserInfo(@RequestBody WXSaveUserInfoRequest request) {
        User user = buildUser(request);
        userService.WXSaveUserInfo(user);
        return ResponseUtil.success();
    }

    @PostMapping("/editPassword")
    public Response<Integer> updatePassword(@RequestBody EditPasswordRequest request) {
        Integer result = userService.updatePassword(request.getUserName(),
                request.getOldPassword(),
                request.getCurPassword());
        return ResponseUtil.success(result);
    }

    private User buildUser(addAdminRequest request) {
        User user = new User();
        user.setUsername(request.getUserName());
        user.setStoreId(request.getStoreId());
        return user;
    }

    private User buildUser(WXSaveUserInfoRequest request) {
        User user = new User();
        user.setUid(request.getUid());
        user.setUsername(request.getUserName());
        user.setPhoneNumber(request.getPhone());
        user.setSex(request.getSex());
        user.setBirthday(LocalDate.parse(request.getBirthday()));
        return user;
    }

    private User buildBO(UserDTO userDTO) {
        return BOUtils.convert(User.class, userDTO);
    }
}

