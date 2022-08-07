package com.wang.productcenter.service;

import com.github.pagehelper.PageInfo;
import com.wang.productcenter.entity.BO.ProductDetail;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 01:03
 * @Description:
 */
public interface IProductDetailService {

    PageInfo<ProductDetail> getAll(ProductDetail productDetail);

    int insert(ProductDetail productDetail);

    ProductDetail getByName(ProductDetail productDetail);

    ProductDetail getById(ProductDetail productDetail);

    void remove(ProductDetail productDetail);

    PageInfo<ProductDetail> getLikeName(ProductDetail productDetail);

    int update(ProductDetail productDetail);

    List<ProductDetail> getByIds(List<Integer> idList);

    Map<Integer,List<ProductDetail>> getByProductIds(List<Integer> idList);

    void productAssociationDetail(int productId,List<Integer> detailId);

    void productDisconnectDetail(int productId,List<Integer> detailId);

    List<Integer> getProductDetailIdsByProductId(int productId);

    int removeProductDetailCache(Integer productDetailId);

    int removeProductDetailCache(List<Integer> productDetailIds);

    void removeMapCache(Integer productDetailId);

    void flush();
}
