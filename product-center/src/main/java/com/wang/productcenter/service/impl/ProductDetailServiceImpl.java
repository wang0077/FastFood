package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.productcenter.dao.ProductDetailDao;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.entity.PO.ProductDetailPO;
import com.wang.productcenter.entity.PO.Product_DetailType_Middle;
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
    public PageInfo<ProductDetail> getAll(ProductDetail productDetail) {
        PageUtils.startPage(productDetail);
        List<ProductDetailPO> result = productDetailDao.getAll();
        List<ProductDetail> productDetails = result
                .stream()
                .map(ProductDetailPO::convertToProductDetail)
                .collect(Collectors.toList());
        getDetailType(productDetails, productDetail.isDetail());
        return PageUtils.getPageInfo(result, productDetails);
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
        getDetailType(productDetailResult);
        return productDetailResult;
    }

    @Override
    public ProductDetail getById(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        ProductDetailPO result = productDetailDao.getById(productDetailPO);
        if (result == null) {
            return null;
        }
        ProductDetail productDetailResult = result.convertToProductDetail();
        getDetailType(productDetailResult);
        return productDetailResult;
    }

    @Override
    public void remove(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        productDetailDao.remove(productDetailPO);
    }

    @Override
    public PageInfo<ProductDetail> getLikeName(ProductDetail productDetail) {
        PageUtils.startPage(productDetail);
        ProductDetailPO productDetailPO = productDetail.doForward();
        List<ProductDetailPO> result = productDetailDao.getLikeName(productDetailPO);
        List<ProductDetail> productDetails = result.stream()
                .map(ProductDetailPO::convertToProductDetail)
                .collect(Collectors.toList());
        getDetailType(productDetails);
        return PageUtils.getPageInfo(result, productDetails);
    }

    @Override
    public int update(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        return productDetailDao.update(productDetailPO);
    }

    @Override
    public List<ProductDetail> getByIds(List<Integer> idList) {
        List<ProductDetailPO> result = productDetailDao.getByIds(idList);
        return result.stream()
                .map(ProductDetailPO::convertToProductDetail)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, List<ProductDetail>> getByProductIds(List<Integer> idList) {

        // 通过ProductId查询中间表获取和ProductDetail关联关系
        List<Product_Detail_Middle> productDetailMiddles = getProductMiddle(idList);

        // 通过ProductId查询中间表获取和DetailType关联关系
        List<Product_DetailType_Middle> productDetailTypeMiddles = detailTypeService.getByProductId(idList);

        // 去重获取所有的ProductDetailId
        List<Integer> productDetailIds = productDetailMiddles.stream()
                .map(Product_Detail_Middle::getProductDetailId)
                .distinct()
                .collect(Collectors.toList());

        // 去重获取所有的DetailTypeId
        List<Integer> detailTypeIds = productDetailTypeMiddles.stream()
                .map(Product_DetailType_Middle::getDetailTypeId)
                .distinct()
                .collect(Collectors.toList());

        // 查询出所有的ProductDetail
        List<ProductDetailPO> result = productDetailDao.getByIds(productDetailIds);

        List<ProductDetail> productDetails = result.stream()
                .map(ProductDetailPO::convertToProductDetail)
                .collect(Collectors.toList());

        // 查询出所有的detailTypes
        List<DetailType> detailTypes = detailTypeService.getByIds(detailTypeIds);

        // 归纳出ProductId和DetailType映射关系
        Map<Integer, List<DetailType>> productMap = productDetailTypeMiddles.stream()
                .collect(Collectors.toMap(Product_DetailType_Middle::getProductId
                        , productId -> detailTypes.stream()
                                .filter(detailType -> detailType.getId().equals(productId.getDetailTypeId()))
                                .collect(Collectors.toList())
                        , (List<DetailType> v1, List<DetailType> v2) -> {
                            v1.addAll(v2);
                            return v1;
                        }));
        // 通过Product和DetailType映射关系再和Product和ProductDetail关系
        // 整合出Product对应的ProductDetail封装和Product对应的DetailType
        return  productDetailMiddles.stream()
                .collect(Collectors.toMap(Product_Detail_Middle::getProductId
                        , middle -> productDetails.stream()
                                .filter(productDetail -> {
                                    Integer productId = middle.getProductId();
                                    List<DetailType> detailTypeList = productMap.get(productId);
                                    List<DetailType> list = detailTypeList.stream()
                                            .filter(detailType -> detailType.getProductDetailId().equals(productDetail.getId()))
                                            .collect(Collectors.toList());
                                    if (list.size() == 0) {
                                        return false;
                                    }
                                    productDetail.setDetailTypeList(list);
                                    return true;
                                })
                                .collect(Collectors.toList())
                        , (List<ProductDetail> v1, List<ProductDetail> v2) -> v1));
    }

    private List<Product_Detail_Middle> getProductMiddle(List<Integer> idList) {
        return productDetailDao.getProductDetailIdByProductId(idList);
    }

    private void getDetailType(ProductDetail productDetail) {
        getDetailType(Lists.newArrayList(productDetail));
    }

    private void getDetailType(List<ProductDetail> productDetails, boolean isDetail) {
        if (!isDetail) {
            return;
        }
        getDetailType(productDetails);
    }

    private void getDetailType(List<ProductDetail> productDetails) {
        List<Integer> idList = productDetails
                .stream()
                .map(ProductDetail::getId)
                .collect(Collectors.toList());
        Map<Integer, List<DetailType>> detailTypes = detailTypeService.groupByProductDetailId(idList);
        productDetails.get(0).setDetailTypeList(detailTypes.get(productDetails.get(0).getId()));
        productDetails
                .forEach(productDetail -> productDetail.setDetailTypeList(detailTypes.get(productDetail.getId())));
    }
}
