package com.wang.productcenter.service.impl;

import com.google.common.collect.Lists;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.productcenter.dao.ProductDetailDao;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.entity.PO.ProductDetailPO;
import com.wang.productcenter.entity.PO.Product_Detail_Middle;
import com.wang.productcenter.service.IDetailTypeService;
import com.wang.productcenter.service.IProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 01:04
 * @Description:
 */

@Service
public class ProductDetailServiceImpl implements IProductDetailService {

    @Autowired
    private ProductDetailDao productDetailDao;

    @Autowired
    private IDetailTypeService detailTypeService;


    @Override
    public List<ProductDetail> getAll(ProductDetail productDetail) {
        PageUtils.startPage(productDetail);
        List<ProductDetailPO> result = productDetailDao.getAll();
        List<ProductDetail> productDetails = result
                .stream()
                .map(ProductDetailPO::convertToProductDetail)
                .collect(Collectors.toList());
        return getDetailType(productDetails);
    }

    public int insert(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        ProductDetailPO result_temp = productDetailDao.getByName(productDetailPO);
        if (result_temp != null) {
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        return productDetailDao.insert(productDetailPO);
    }

    public ProductDetail getByName(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        ProductDetailPO result = productDetailDao.getByName(productDetailPO);
        if (result == null) {
            return null;
        }
        ProductDetail productDetailResult = result.convertToProductDetail();
        return getDetailType(productDetailResult);
    }

    @Override
    public ProductDetail getById(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        ProductDetailPO result = productDetailDao.getById(productDetailPO);
        if (result == null) {
            return null;
        }
        ProductDetail productDetailResult = result.convertToProductDetail();
        return getDetailType(productDetailResult);
    }

    @Override
    public void remove(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        productDetailDao.remove(productDetailPO);
    }

    @Override
    public List<ProductDetail> getLikeName(ProductDetail productDetail) {
        PageUtils.startPage(productDetail);
        ProductDetailPO productDetailPO = productDetail.doForward();
        List<ProductDetailPO> result = productDetailDao.getLikeName(productDetailPO);
        List<ProductDetail> productDetails = result.stream()
                .map(ProductDetailPO::convertToProductDetail)
                .collect(Collectors.toList());
        return getDetailType(productDetails);
    }

    @Override
    public int update(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        return productDetailDao.update(productDetailPO);
    }

    @Override
    public Map<Integer, List<ProductDetail>> getByProductIds(List<Integer> idList) {

        // 通过ProductId查询中间表获取和ProductDetail关联关系
        List<Product_Detail_Middle> middles = getProductMiddle(idList);

        // 去重获取所有的ProductDetailId
        List<Integer> productDetailIds = middles.stream()
                .map(Product_Detail_Middle::getProductDetailId)
                .distinct()
                .collect(Collectors.toList());

        // 查询出所有的ProductDetail
        List<ProductDetailPO> result = productDetailDao.getByIds(productDetailIds);
        List<ProductDetail> productDetails = result.stream()
                .map(ProductDetailPO::convertToProductDetail)
                .collect(Collectors.toList());

        // 把ProductDetail关联的DetailType查询出来并储存
        getDetailType(productDetails);

        // 返回一个Map<ProductId,List -> ProductDetail> 结构的数据
        return middles.stream()
                .collect(Collectors.toMap(Product_Detail_Middle::getProductId,
                        middle -> productDetails.stream()
                                .filter(productDetail -> {
                                    return productDetail.getId().equals(middle.getProductDetailId());
                                })
                                .collect(Collectors.toList()),
                        (List<ProductDetail> n1, List<ProductDetail> n2) -> {
                            n1.addAll(n2);
                            return n1;
                        }));
    }

    private List<Product_Detail_Middle> getProductMiddle(List<Integer> idList) {
        return productDetailDao.getProductDetailIdByProductId(idList);
    }

    private ProductDetail getDetailType(ProductDetail productDetail) {
        return getDetailType(Lists.newArrayList(productDetail)).get(0);
    }

    private List<ProductDetail> getDetailType(List<ProductDetail> productDetails) {
        List<Integer> idList = productDetails
                .stream()
                .map(ProductDetail::getId)
                .collect(Collectors.toList());
        Map<Integer, List<DetailType>> detailTypes = detailTypeService.groupByProductDetailId(idList);
        productDetails.get(0).setDetailTypeList(detailTypes.get(productDetails.get(0).getId()));
        productDetails
                .forEach(productDetail -> productDetail.setDetailTypeList(detailTypes.get(productDetail.getId())));
        return productDetails;
    }
}
