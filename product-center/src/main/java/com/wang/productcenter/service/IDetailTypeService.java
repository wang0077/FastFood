package com.wang.productcenter.service;

import com.wang.productcenter.entity.BO.DetailType;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 02:10
 * @Description:
 */
public interface IDetailTypeService {

    List<DetailType> getAll();

    int insert(DetailType detailType);

    DetailType getById(DetailType detailType);

    DetailType getByName(DetailType detailType);

    List<DetailType> getLikeName(DetailType detailType);

    void remove(DetailType detailType);

    int update(DetailType detailType);
}
