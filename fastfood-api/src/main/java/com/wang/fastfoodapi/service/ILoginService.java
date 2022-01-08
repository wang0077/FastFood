package com.wang.fastfoodapi.service;

import com.wang.fastfood.apicommons.entity.DTO.UserDTO;

/**
 * @Auther: wAnG
 * @Date: 2022/1/3 18:51
 * @Description:
 */
public interface ILoginService {

    String login(UserDTO userDTO);

}
