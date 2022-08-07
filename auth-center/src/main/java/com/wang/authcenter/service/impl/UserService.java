package com.wang.authcenter.service.impl;

import com.wang.authcenter.service.IUserService;
import com.wang.authcenter.service.Remote.UserRemote;
import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import com.wang.fastfood.apicommons.entity.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 11:57
 * @Description:
 */

@Service
public class UserService implements UserDetailsService, IUserService {

    @Autowired
    private UserRemote userRemote;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = userRemote.getByName(username);
        if(user == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return user;
    }

    public UserDetails loadUserByOpenId(String openId) {
        Response<UserDTO> response = userRemote.getById(openId);
        if(response.getData() == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return response.getData();
    }


    @Override
    public String login(UserDTO userDTO, HttpServletRequest request) {
//        String userName = userDTO.getUsername();
//        String passWord = userDTO.getPassword();
//        UserDetails user = loadUserByUsername(userName);
//        if(user == null || !passwordEncoder.matches(passWord,user.getPassword())){
//            //todo 返回密码或用户名错误
//        }
        return null;

    }
}
