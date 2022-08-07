package com.wang.accountcenter.service.impl;

import com.wang.accountcenter.config.ExperienceProperties;
import com.wang.accountcenter.dao.UserAccountDao;
import com.wang.accountcenter.entity.BO.ExperienceLevel;
import com.wang.accountcenter.entity.BO.SignIn;
import com.wang.accountcenter.entity.BO.UserAccount;
import com.wang.accountcenter.entity.PO.UserAccountPO;
import com.wang.accountcenter.exception.PayException;
import com.wang.accountcenter.exception.PaymentAmountException;
import com.wang.accountcenter.exception.UserAccountAlreadyExistException;
import com.wang.accountcenter.service.ISignInService;
import com.wang.accountcenter.service.IUserAccountService;
import com.wang.fastfood.apicommons.Util.IDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired
    private ExperienceProperties properties;

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
            if (signIn != null) {
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

    @Override
    public Double getUserAmount(String openId) {
        return userAccountDao.getUserAmount(openId);
    }

    @Override
    public void rechargeAmount(UserAccount userAccount) {
        UserAccountPO userAccountPO = userAccount.doForward();
        userAccountDao.rechargeAmount(userAccountPO);
    }

    @Override
    public void pay(String OpenId, Double amount) {
        if (amount > 0) {
            int result = userAccountDao.pay(OpenId, amount);
            if (result == 0) {
                throw new PayException("支付异常");
            }
        } else {
            throw new PaymentAmountException("支付金额异常");
        }
    }

    /**
     * 计算该订单积分和经验
     */
    @Override
    public Integer calculateOrderInterest(String openId, Double amount) {
        UserAccount userAccount = buildUserAccount(openId);
        UserAccount userInfo = getByUserId(userAccount);
        calculateExperience(amount, userInfo);
        IsUpgrade(userInfo);
        calculateIntegral(amount,userInfo);
        return userAccountDao.addExperienceAndIntegral(userInfo.doForward());
    }

    public void IsUpgrade(UserAccount userInfo) {
        Integer experience = userInfo.getExperience();
        List<ExperienceLevel> levels = properties.getLevels();
        for (ExperienceLevel level : levels) {
            if (experience > level.getNeedExperience()) {
                userInfo.setUserLevel(level.getLevel());
            } else {
                userInfo.setNeedExperience(level.getNeedExperience());
                break;
            }
        }
    }

    private void calculateIntegral(Double amount,UserAccount userInfo){
        int Integral = (int) Math.ceil(amount * 5.0);
        userInfo.setIntegral(userInfo.getIntegral() + Integral);
    }

    private void calculateExperience(Double amount, UserAccount userInfo) {
        int experience = (int) Math.ceil(amount * 2.0);
        userInfo.setExperience(userInfo.getExperience() + experience);
    }

    private void allowedCheckIn(SignIn signIn) {
        LocalDateTime lastTime = signIn.getLastTime();
        Duration betweenTime = Duration.between(LocalDateTime.now(), lastTime);
        signIn.setAllowedCheckIn(betweenTime.toDays() != 0);
    }

    private UserAccount buildUserAccount(String openId) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUid(openId);
        return userAccount;
    }


    private SignIn buildSignIn(String uid) {
        SignIn signIn = new SignIn();
        signIn.setUid(uid);
        return signIn;
    }
}
