package com.wang.accountcenter.service.impl;

import com.wang.accountcenter.dao.TakeawayAddressDao;
import com.wang.accountcenter.entity.BO.TakeawayAddress;
import com.wang.accountcenter.entity.PO.TakeawayAddressPO;
import com.wang.accountcenter.service.ITakeawayAddressService;
import com.wang.fastfood.apicommons.Util.IDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/4/12 02:54
 * @Description:
 */

@Service
public class TakeawayAddressServiceImpl implements ITakeawayAddressService {

    @Autowired
    private TakeawayAddressDao takeawayAddressDao;

    @Override
    public void insert(TakeawayAddress takeawayAddress) {
        TakeawayAddressPO takeawayAddressPO = takeawayAddress.doForward();
        // 如果存在ID就是更新操作
        if(takeawayAddressPO.getAddressId() != null){
            takeawayAddressDao.update(takeawayAddressPO);
        }else {
            takeawayAddressPO.setAddressId("AD" + IDUtil.getID());
            takeawayAddressDao.insert(takeawayAddressPO);
        }
    }

    public List<TakeawayAddress> getByUid(String openId){
        List<TakeawayAddressPO> result = takeawayAddressDao.getByUid(openId);
        return result.stream()
                .map(TakeawayAddressPO::convertToTakeawayAddress)
                .collect(Collectors.toList());
    }
}
