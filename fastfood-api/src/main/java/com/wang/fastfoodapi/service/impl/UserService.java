package com.wang.fastfoodapi.service.impl;

import com.wang.fastfood.apicommons.entity.DTO.UserDTO;
import com.wang.fastfoodapi.service.Remote.UserRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 11:57
 * @Description:
 */

@Service
public class UserService implements ReactiveUserDetailsService {

    @Autowired
    private UserRemote userRemote;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserDTO user = userRemote.getByName(username);
//        if(user == null){
//            throw new UsernameNotFoundException("用户不存在");
//        }
//        return user;
//    }


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        UserDTO user = userRemote.getByName(username);
        if(user == null){
            throw new UsernameNotFoundException("用户不存在");
        }

        return null;
    }
}
