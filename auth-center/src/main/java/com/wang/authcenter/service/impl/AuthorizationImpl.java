package com.wang.authcenter.service.impl;

import com.wang.authcenter.dao.AuthorizationDao;
import com.wang.authcenter.entity.Authorization;
import com.wang.authcenter.service.IAuthorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/4 21:39
 * @Description:
 */

@Service
public class AuthorizationImpl implements IAuthorization {

    @Autowired
    private AuthorizationDao authorizationDao;

    @Override
    public List<Authorization> getAuthorization() {

        return authorizationDao.getAuthorization();
    }
}
