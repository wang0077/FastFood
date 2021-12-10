package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.productcenter.dao.ProductTypeDao;
import com.wang.productcenter.entity.BO.ProductType;
import com.wang.productcenter.entity.PO.ProductTypePO;
import com.wang.productcenter.service.IProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 21:00
 * @Description:
 */

@Service
public class ProductTypeServiceImpl implements IProductTypeService {

    @Autowired
    private ProductTypeDao productTypeDao;

    @Override
    public PageInfo<ProductType> getAll(ProductType productType) {
        PageUtils.startPage(productType);
        List<ProductTypePO> result = productTypeDao.getAll();
        return PageUtils.getPageInfo(result, result
                .stream()
                .map(ProductTypePO::convertToProductType)
                .collect(Collectors.toList()));
    }

    @Override
    public int insert(ProductType productType) {
        ProductTypePO productTypePO = productType.doForward();
        ProductTypePO result = productTypeDao.getByName(productTypePO);
        if (result != null) {
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        return productTypeDao.insert(productTypePO);
    }

    @Override
    public ProductType getById(ProductType productType) {
        ProductTypePO productTypePO = productType.doForward();
        ProductTypePO result = productTypeDao.getById(productTypePO);
        if (result == null) {
            return null;
        }
        return result.convertToProductType();
    }

    public PageInfo<ProductType> getLikeName(ProductType productType) {
        ProductTypePO productTypePO = productType.doForward();
        List<ProductTypePO> result = productTypeDao.getLikeName(productTypePO);
        return PageUtils.getPageInfo(result,result
                .stream()
                .map(ProductTypePO::convertToProductType)
                .collect(Collectors.toList()));
    }

    public ProductType getByName(ProductType productType) {
        ProductTypePO productTypePO = productType.doForward();
        ProductTypePO result = productTypeDao.getByName(productTypePO);
        return result != null ? result.convertToProductType() : null;
    }

    public void removeType(ProductType productType) {
        ProductTypePO productTypePO = productType.doForward();
        productTypeDao.remove(productTypePO);
    }

    public int updateType(ProductType productType) {
        ProductTypePO productTypePO = productType.doForward();
        return productTypeDao.update(productTypePO);
    }

    @Override
    public Map<Integer, ProductType> groupById(List<Integer> idList) {
        List<ProductTypePO> result = getByIdList(idList);
        List<ProductType> productTypes = result.stream()
                .map(ProductTypePO::convertToProductType)
                .collect(Collectors.toList());
        return productTypes.stream().collect(Collectors.toMap(ProductType::getId, productType -> productType));
    }

    private List<ProductTypePO> getByIdList(List<Integer> idList) {
        return productTypeDao.groupById(idList);
    }

}
