package com.wang.productcenter.service.impl;

import com.wang.productcenter.dao.DetailTypeDao;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.entity.PO.DetailTypePO;
import com.wang.productcenter.service.IDetailTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 02:11
 * @Description:
 */

@Service
public class DetailTypeServiceImpl implements IDetailTypeService {

    @Autowired
    DetailTypeDao detailTypeDao;

    @Override
    public List<DetailType> getAll() {
        List<DetailTypePO> result = detailTypeDao.getAll();
        return result.stream().map(DetailTypePO::convertToDetailType).collect(Collectors.toList());
    }

    @Override
    public DetailType getById(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        DetailTypePO result = detailTypeDao.getById(detailTypePO);
        return result.convertToDetailType();
    }

    @Override
    public List<DetailType> getByName(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        List<DetailTypePO> result = detailTypeDao.getByName(detailTypePO);
        return result.stream().map(DetailTypePO::convertToDetailType).collect(Collectors.toList());
    }

    @Override
    public void remove(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        detailTypeDao.remove(detailTypePO);
    }

    @Override
    public void update(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        detailTypeDao.update(detailTypePO);
    }
}
