package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.productcenter.dao.DetailTypeDao;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.entity.PO.DetailTypePO;
import com.wang.productcenter.service.IDetailTypeService;
import com.wang.productcenter.service.IProductDetailService;
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

    @Autowired
    IProductDetailService productDetailService;

    public int insert(DetailType detailType){
        DetailTypePO detailTypePO = detailType.doForward();
        // 商品详情ID未传入或商品详情ID在对应表不存在返回插入失败
        if(detailTypePO.getProductDetailId() == null || validityProductDetailId(detailType)){
            return SqlResultEnum.ERROR_INSERT.getValue();
        }
        DetailTypePO result = detailTypeDao.getByName(detailTypePO);
        if(result != null){
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        return detailTypeDao.insert(detailTypePO);
    }

    @Override
    public PageInfo<DetailType> getAll(DetailType detailType) {
        PageUtils.startPage(detailType);
        List<DetailTypePO> result = detailTypeDao.getAll();
        List<DetailType> detailTypeList = result.stream()
                .map(DetailTypePO::convertToDetailType)
                .collect(Collectors.toList());
        getProductDetail(detailTypeList);
        return PageUtils.getPageInfo(result,detailTypeList);
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
    public PageInfo<DetailType> getLikeName(DetailType detailType){
        PageUtils.startPage(detailType);
        DetailTypePO detailTypePO = detailType.doForward();
        List<DetailTypePO> result = detailTypeDao.getLikeName(detailTypePO);
        return PageUtils.getPageInfo(result,result
                .stream()
                .map(DetailTypePO::convertToDetailType)
                .collect(Collectors.toList()));
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
    public PageInfo<DetailType> getByProductDetailId(DetailType detailType) {
        PageUtils.startPage(detailType);
        DetailTypePO detailTypePO = detailType.doForward();
        return getByProductDetailId(Lists.newArrayList(detailTypePO.getProductDetailId()));
    }

    @Override
    public Map<Integer,List<DetailType>> groupByProductDetailId(List<Integer> idList) {
        PageInfo<DetailType> result = getByProductDetailId(idList);
        List<DetailType> detailTypes = result.getList();
        return detailTypes.stream()
                .collect(Collectors.groupingBy(DetailType::getProductDetailId));
    }

    private void getProductDetail(List<DetailType> detailTypes){
        List<Integer> idList = detailTypes.stream()
                .map(DetailType::getProductDetailId)
                .collect(Collectors.toList());
        List<ProductDetail> productDetails = productDetailService.getByIds(idList);
        productDetails.forEach(productDetail -> {
            detailTypes.forEach(detailType -> {
                if(detailType.getProductDetailId().equals(productDetail.getId())){
                    detailType.setProductDetail(productDetail);
                }
            });
        });
    }

    private boolean validityProductDetailId(DetailType detailType){
        ProductDetail productDetail = new ProductDetail();
        productDetail.setId(detailType.getProductDetailId());
        ProductDetail result = productDetailService.getById(productDetail);
        return result == null;
    }

    private PageInfo<DetailType> getByProductDetailId(List<Integer> idList){
        List<DetailTypePO> result = detailTypeDao.getByProductDetailId(idList);
        List<DetailType> detailTypeList = result.stream()
                .map(DetailTypePO::convertToDetailType)
                .collect(Collectors.toList());
        return PageUtils.getPageInfo(result,detailTypeList);
    }

}
