package com.wang.productcenter.service.impl;

import com.wang.productcenter.entity.BO.ProductType;
import com.wang.productcenter.dao.ProductTypeDao;
import com.wang.productcenter.entity.PO.ProductTypePO;
import com.wang.productcenter.service.IProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/11/30 21:00
 * @Description:
 */

@Service
public class ProductTypeServiceImpl implements IProductTypeService {

    @Autowired
    ProductTypeDao productTypeDao;

    @Override
    public List<ProductType> getAll() {
        List<ProductTypePO> typePOList = productTypeDao.getAll();
        return typePOList.stream().map(ProductTypePO::convertToProductType).collect(Collectors.toList());
    }

    @Override
    public ProductType getById(ProductType productType) {
        ProductTypePO productTypePO = productType.convertToProductTypePO();
        ProductTypePO productTypeWithId = productTypeDao.getById(productTypePO);
        if(productTypeWithId == null){
            return null;
        }
        return productTypeWithId.convertToProductType();
    }

    public List<ProductType> getByName(ProductType productType){
        ProductTypePO productTypePO = productType.convertToProductTypePO();
        List<ProductTypePO> productTypePOList = productTypeDao.getByName(productTypePO);
        return productTypePOList.stream().map(ProductTypePO::convertToProductType).collect(Collectors.toList());
    }

    public void removeType(ProductType productType){
        ProductTypePO productTypePO = productType.convertToProductTypePO();
        productTypeDao.remove(productTypePO);
    }

    public void updateType(ProductType productType){
        ProductTypePO productTypePO = productType.convertToProductTypePO();
        productTypeDao.update(productTypePO);
    }
}
