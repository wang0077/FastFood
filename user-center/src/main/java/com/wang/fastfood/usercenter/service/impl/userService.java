package com.wang.fastfood.usercenter.service.impl;

import com.wang.fastfood.apicommons.Util.EncoderUtil;
import com.wang.fastfood.apicommons.Util.IdUtil;
import com.wang.fastfood.usercenter.dao.UserDao;
import com.wang.fastfood.usercenter.entity.BO.User;
import com.wang.fastfood.usercenter.entity.PO.UserPO;
import com.wang.fastfood.usercenter.exception.UserAlreadyExistException;
import com.wang.fastfood.usercenter.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 12:07
 * @Description:
 */

@Service
public class userService implements IUserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getByUserName(String userName) {
        UserPO user = userDao.getByUserName(userName);
        if(user == null){
            return null;
        }
        return user.convertToDetailType();
    }

    @Override
    public Integer add(User user) throws UserAlreadyExistException {
        User userName = getByUserName(user.getUsername());
        if(userName != null){
            throw new UserAlreadyExistException("用户已存在");
        }
        user.setPassword(EncoderUtil.encoder(user.getPassword()));
        user.setUid("U" + IdUtil.getID());
        user.setUserLevel(0);
        user.setExperience(0);
        UserPO userPO = user.doForward();
        return userDao.add(userPO);
    }
}
