package com.wang.fastfood.usercenter.service.impl;

import com.wang.fastfood.apicommons.Util.EncoderUtil;
import com.wang.fastfood.apicommons.Util.IDUtil;
import com.wang.fastfood.usercenter.dao.UserDao;
import com.wang.fastfood.usercenter.entity.BO.User;
import com.wang.fastfood.usercenter.entity.PO.UserPO;
import com.wang.fastfood.usercenter.exception.PasswordException;
import com.wang.fastfood.usercenter.exception.RegisterParamMiss;
import com.wang.fastfood.usercenter.exception.UserAlreadyExistException;
import com.wang.fastfood.usercenter.exception.UserNotExistException;
import com.wang.fastfood.usercenter.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/1/4 12:07
 * @Description:
 */

@Service
public class userService implements IUserService {

    @Autowired
    private UserDao userDao;

    private static final String DEFAULT_PASSWORD = "123456";

    private static final Integer USER_ROLE = 1;

    private static final Integer STORE_ADMIN_ROLE = 2;

    @Override
    public User getByUserName(String userName) {
        UserPO user = userDao.getByUserName(userName);
        if (user == null) {
            return null;
        }
        return user.convertToUser();
    }

    @Override
    public User getByUserId(String uid) {
        UserPO user = userDao.getByUserId(uid);
        if (user == null) {
            return null;
        }
        return user.convertToUser();
    }

    @Override
    public List<User> getByUserIds(List<String> uidList) {
        List<UserPO> userPO = userDao.getByUserIds(uidList);
        return userPO.stream().map(UserPO::convertToUser).collect(Collectors.toList());
    }

    @Override
    public Integer add(User user) {
        User userName = getByUserName(user.getUsername());
        if (userName != null) {
            throw new UserAlreadyExistException("用户已存在");
        }
        if (user.getPassword() != null) {
            user.setPassword(EncoderUtil.encoder(user.getPassword()));
        }
        if (user.getUid() == null) {
            user.setUid("U" + IDUtil.getID());
        }
        user.setUserLevel(0);
        user.setExperience(0);
        user.setRole(USER_ROLE);
        UserPO userPO = user.doForward();
        return userDao.add(userPO);
    }


    @Override
    public Integer addStoreAdmin(User user) {
        String storeId = user.getStoreId();
        if (storeId == null) {
            throw new RegisterParamMiss("缺少门店ID");
        }
        User existUser = getByStoreId(storeId);
        if (existUser != null) {
            throw new UserAlreadyExistException("该门店已存在管理员账户");
        }
        if (user.getUid() == null) {
            user.setUid("U" + IDUtil.getID());
        }
        if (user.getPassword() != null) {
            user.setPassword(EncoderUtil.encoder(user.getPassword()));
        } else {
            user.setPassword(EncoderUtil.encoder(DEFAULT_PASSWORD));
        }
        user.setRole(STORE_ADMIN_ROLE);
        return userDao.addAdmin(user.doForward());
    }

    @Override
    public User getByStoreId(String storeId) {
        UserPO userPO = userDao.getByStoreId(storeId);
        return userPO != null ? userPO.convertToUser() : null;
    }

    @Override
    public User WXGetUserInfo(String openId) {
        UserPO userPO = userDao.WXGetUserInfo(openId);
        if (userPO == null) {
            throw new UserNotExistException("用户不存在");
        }
        return userPO.convertToUser();
    }

    @Override
    public void WXSaveUserInfo(User user) {
        UserPO userPO = user.doForward();
        userDao.WXSaveUserInfo(userPO);
    }

    @Override
    public Integer updatePassword(String userName, String oldPassword, String curPassword) {
        UserPO user = userDao.getByUserName(userName);
        if (user != null) {
            if (EncoderUtil.match(oldPassword,user.getPassword())) {
                String curEncoder = EncoderUtil.encoder(curPassword);
                return userDao.updatePassword(curEncoder, userName);
            } else {
                throw new PasswordException("旧密码存在错误!");
            }
        } else {
            throw new UserNotExistException("该用户不存在!");
        }
    }
}
