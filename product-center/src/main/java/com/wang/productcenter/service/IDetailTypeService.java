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

    DetailType getById(DetailType detailType);

    List<DetailType> getByName(DetailType detailType);

    void remove(DetailType detailType);

    void update(DetailType detailType);
}
