package com.wang.productcenter.service;

import com.github.pagehelper.PageInfo;
import com.wang.productcenter.entity.BO.DetailType;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wAnG
 * @Date: 2021/12/2 02:10
 * @Description:
 */
public interface IDetailTypeService {

    PageInfo<DetailType> getAll(DetailType detailType);

    int insert(DetailType detailType);

    DetailType getById(DetailType detailType);

    DetailType getByName(DetailType detailType);

    PageInfo<DetailType> getLikeName(DetailType detailType);

    void remove(DetailType detailType);

    int update(DetailType detailType);

    Map<Integer,List<DetailType>> groupByProductDetailId(List<Integer> idList);

    PageInfo<DetailType> getByProductDetailId(DetailType detailType);
}
