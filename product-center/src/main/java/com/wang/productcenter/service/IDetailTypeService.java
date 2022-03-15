package com.wang.productcenter.service;

import com.github.pagehelper.PageInfo;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.entity.PO.Product_DetailType_Middle;

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

    List<Product_DetailType_Middle> getByProductMiddle(List<Integer> idList);

    List<DetailType> getByIds(List<Integer> idList);

    void productAssociationDetailType(int productId,List<Integer> detailTypeIds);

    void productDisconnectDetailType(int productId, List<Integer> detailTypeIds);

    List<Integer> getDetailTypeIdsByProductId(int productId);

    void flush();
}
