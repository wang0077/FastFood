package com.wang.productcenter.service.impl;

import com.google.common.collect.Lists;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.productcenter.dao.DetailTypeDao;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.entity.PO.DetailTypePO;
import com.wang.productcenter.service.IDetailTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    public int insert(DetailType detailType){
        DetailTypePO detailTypePO = detailType.doForward();
        // 商品详情ID未传入或商品详情ID在对应表不存在返回插入失败
        if(detailTypePO.getProductDetailId() == null || getByProductDetailId(detailType).size() == 0){
            return SqlResultEnum.ERROR_INSERT.getValue();
        }
        DetailTypePO result = detailTypeDao.getByName(detailTypePO);
        if(result != null){
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        return detailTypeDao.insert(detailTypePO);
    }

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
    public DetailType getByName(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        DetailTypePO result = detailTypeDao.getByName(detailTypePO);
        return result != null ? result.convertToDetailType() : null;
    }

    @Override
    public List<DetailType> getLikeName(DetailType detailType){
        DetailTypePO detailTypePO = detailType.doForward();
        List<DetailTypePO> result = detailTypeDao.getLikeName(detailTypePO);
        return result.stream().map(DetailTypePO::convertToDetailType).collect(Collectors.toList());
    }

    @Override
    public void remove(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        detailTypeDao.remove(detailTypePO);
    }

    @Override
    public int update(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        return detailTypeDao.update(detailTypePO);
    }

    @Override
    public List<DetailType> getByProductDetailId(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        List<DetailTypePO> result = getByProductDetailId(Lists.newArrayList(detailTypePO.getProductDetailId()));
        return result.stream().map(DetailTypePO::convertToDetailType).collect(Collectors.toList());
    }

    @Override
    public Map<Integer,List<DetailType>> groupByProductDetailId(List<Integer> idList) {
        List<DetailTypePO> result = getByProductDetailId(idList);
        List<DetailType> detailTypes = result.stream()
                .map(DetailTypePO::convertToDetailType)
                .collect(Collectors.toList());
        return detailTypes.stream()
                .collect(Collectors.groupingBy(DetailType::getProductDetailId));
    }

    private List<DetailTypePO> getByProductDetailId(List<Integer> idList){
        return detailTypeDao.getByProductDetailId(idList);
    }

}
