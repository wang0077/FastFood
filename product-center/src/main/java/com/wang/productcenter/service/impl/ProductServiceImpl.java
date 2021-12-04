package com.wang.productcenter.service.impl;

import com.wang.productcenter.dao.ProductDao;
import com.wang.productcenter.entity.BO.Product;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.entity.BO.ProductType;
import com.wang.productcenter.entity.PO.ProductPO;
import com.wang.productcenter.service.IProductDetailService;
import com.wang.productcenter.service.IProductService;
import com.wang.productcenter.service.IProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2021/12/4 21:24
 * @Description:
 */

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private IProductTypeService productTypeService;

    @Autowired
    private IProductDetailService productDetailService;

    public List<Product> getAll(){
        List<ProductPO> result = productDao.getAll();
        List<Product> products = result.stream()
                .map(ProductPO::convertToProduct)
                .collect(Collectors.toList());
        getProductType(products);
        getProductDetail(products);
        return products;
    }

    private void getProductDetail(List<Product> products){
        List<Integer> idList = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        Map<Integer, List<ProductDetail>> productDetail = productDetailService.getByProductIds(idList);
        products.forEach(product -> product.setProductDetailList(productDetail.get(product.getId())));
    }

    private void getProductType(List<Product> products){
        List<Integer> idList = products.stream()
                .map(Product::getTypeId)
                .collect(Collectors.toList());
        Map<Integer, ProductType> result = productTypeService.groupById(idList);
        products.forEach(product -> product.setProductType(result.get(product.getTypeId())));
    }

}
