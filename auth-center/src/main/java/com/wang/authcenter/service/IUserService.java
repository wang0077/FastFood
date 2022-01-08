package com.wang.authcenter.service;

import com.wang.fastfood.apicommons.entity.DTO.UserDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 21:29
 * @Description:
 */
public interface IUserService {

    String login(UserDTO userDTO, HttpServletRequest request);

}
