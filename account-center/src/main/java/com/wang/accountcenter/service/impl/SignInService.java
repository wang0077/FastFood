package com.wang.accountcenter.service.impl;

import com.wang.accountcenter.dao.SignInDao;
import com.wang.accountcenter.entity.BO.SignIn;
import com.wang.accountcenter.entity.BO.UserAccount;
import com.wang.accountcenter.entity.PO.SignInPO;
import com.wang.accountcenter.exception.CheckInException;
import com.wang.accountcenter.service.ISignInService;
import com.wang.accountcenter.service.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/1/15 21:00
 * @Description:
 */

@Service
public class SignInService implements ISignInService {

    private static final List<Integer> point = new ArrayList<>();

    // todo 后期改到阿波罗上进行配置
    static {
        point.add(1);
        point.add(3);
        point.add(5);
        point.add(7);
        point.add(9);
        point.add(11);
        point.add(13);
    }

    @Autowired
    private SignInDao signInDao;

    @Autowired
    private IUserAccountService userAccountService;

    public SignIn getByUid(SignIn signIn) {
        SignInPO signInPO = signIn.doForward();
        SignInPO result = signInDao.getByUid(signInPO);
        return null;
    }

    @Override
    public int insert(SignIn signIn) {
        SignInPO signInPO = signIn.doForward();
        signInPO.setLastTime(LocalDateTime.now());
        return signInDao.insert(signInPO);
    }

    @Override
    public SignIn checkIn(SignIn signIn) {
        SignIn signInfo = getByUid(signIn);
        LocalDateTime lastTime = signInfo.getLastTime();
        Integer count = signInfo.getContinuousCount();
        Duration betweenTime = Duration.between(LocalDateTime.now(), lastTime);
        if (betweenTime.toDays() == 1) {
            count += 1;
        } else if(betweenTime.toDays() > 1){
            count = 1;
        }
        signInfo.setContinuousCount(count);
        signInfo.setLastTime(lastTime);
        signInfo.setCheckInPoint(getCheckInPoint(count));
        SignInPO signInPO = signInfo.doForward();
        int result = signInDao.checkIn(signInPO);
        if(result > 0){
            UserAccount userAccount = buildUserAccount(signIn.getUid(), signIn.getCheckInPoint());
            result = userAccountService.checkIn(userAccount);
        }
        if(result <= 0){
            throw new CheckInException("签到失败");
        }
        return signIn;
    }

    @Override
    public SignIn checkInInfo(SignIn signIn) {
        return getByUid(signIn);
    }

    private UserAccount buildUserAccount(String uid,int addIntegral){
        UserAccount userAccount = new UserAccount();
        userAccount.setUid(uid);
        userAccount.setAddIntegral(addIntegral);
        return userAccount;
    }

    private int getCheckInPoint(int count){
        if(count > point.size()){
            return point.get(point.size() - 1);
        }else {
            return point.get(count);
        }
    }


}
