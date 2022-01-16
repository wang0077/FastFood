package com.wang.accountcenter.service.impl;

import com.wang.accountcenter.dao.UserAccountDao;
import com.wang.accountcenter.entity.BO.SignIn;
import com.wang.accountcenter.entity.BO.UserAccount;
import com.wang.accountcenter.entity.PO.UserAccountPO;
import com.wang.accountcenter.exception.UserAccountAlreadyExistException;
import com.wang.accountcenter.service.ISignInService;
import com.wang.accountcenter.service.IUserAccountService;
import com.wang.fastfood.apicommons.Util.IDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 17:22
 * @Description:
 */

@Service
public class UserAccountService implements IUserAccountService {

    @Autowired
    private UserAccountDao userAccountDao;

    @Autowired
    private ISignInService signInService;

    @Override
    public int insert(UserAccount userAccount) {
        int result;
        if (getByUserId(userAccount) != null) {
            throw new UserAccountAlreadyExistException("该用户账户已存在,请联系客服!");
        }
        String uid = userAccount.getUid();
        userAccount.setAccountId("UA" + IDUtil.getID());
        UserAccountPO userAccountPO = userAccount.doForward();
        result = userAccountDao.insert(userAccountPO);
        if (result > 0) {
            SignIn signIn = buildSignIn(uid);
            result = signInService.insert(signIn);
        }
        return result;
    }

    @Override
    public UserAccount getByUserId(UserAccount userAccount) {
        UserAccount result = null;
        UserAccountPO userAccountPO = userAccount.doForward();
        UserAccountPO resultPO = userAccountDao.getByUserId(userAccountPO);
        if (resultPO != null) {
            result = resultPO.convertToUserAccount();
            String uid = userAccount.getUid();
            SignIn buildSignIn = buildSignIn(uid);
            SignIn signIn = signInService.getByUid(buildSignIn);
            if(signIn != null){
                allowedCheckIn(signIn);
                result.setSignIn(signIn);
            }
        }
        return result;
    }

    @Override
    public int checkIn(UserAccount userAccount) {
        UserAccountPO userAccountPO = userAccount.doForward();
        return userAccountDao.checkIn(userAccountPO);
    }

    private void allowedCheckIn(SignIn signIn) {
        LocalDateTime lastTime = signIn.getLastTime();
        Duration betweenTime = Duration.between(LocalDateTime.now(), lastTime);
        signIn.setAllowedCheckIn(betweenTime.toDays() != 0);
    }


    private SignIn buildSignIn(String uid) {
        SignIn signIn = new SignIn();
        signIn.setUid(uid);
        return signIn;
    }
}
