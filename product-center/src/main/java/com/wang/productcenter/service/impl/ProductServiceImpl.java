package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.productcenter.Util.RedisUtil;
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

    @Autowired
    private RedisService redisService;

    private static final String REDIS_PREFIX = "Product-";

    private static final String REDIS_PAGE = "page-";

    public PageInfo<Product> getAll(Product product) {
        PageInfo<Product> result = null;
        String redisName = getRedisName(product);
        result = RedisUtil.getByPageInfo(redisName, Product.class);
        if(result == null){
            PageUtils.startPage(product);
            List<ProductPO> poResult = productDao.getAll();
            List<Product> products = poResult.stream()
                    .map(ProductPO::convertToProduct)
                    .collect(Collectors.toList());
            getProductType(products);
            getProductDetail(products);
            result = PageUtils.getPageInfo(poResult, products);
            redisService.set(redisName,result);
        }
        return result;
    }

    @Override
    public Product getById(Product product) {
        ProductPO productPO = product.doForward();
        ProductPO result = productDao.getById(productPO);
        if (result == null) {
            return null;
        }
        Product productResult = result.convertToProduct();
        getProductDetailAndProductType(productResult);
        return productResult;
    }

    @Override
    public Product getByName(Product product) {
        ProductPO productPO = product.doForward();
        ProductPO result = productDao.getByName(productPO);
        if (result == null) {
            return null;
        }
        Product productResult = result.convertToProduct();
        getProductDetailAndProductType(productResult);
        return productResult;
    }

    @Override
    public PageInfo<Product> likeByName(Product product) {
        PageUtils.startPage(product);
        ProductPO productPO = product.doForward();
        List<ProductPO> result = productDao.likeByName(productPO);
        List<Product> productList = result.stream()
                .map(ProductPO::convertToProduct)
                .collect(Collectors.toList());
        getProductDetailAndProductType(productList);
        return PageUtils.getPageInfo(result, productList);
    }

    @Override
    public void remove(Product product) {
        ProductPO productPO = product.doForward();
        productDao.remove(productPO);
    }

    @Override
    public int update(Product product) {
        ProductPO productPO = product.doForward();
        return productDao.update(productPO);
    }

    private String getRedisName(Product product) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (product != null) {
            if (((Page) product).IsPage()) {
                result.append(REDIS_PAGE);
                result.append(product.getPageNum());
            } else {
                if(product.getId() == null){
                    result.append("ALL");
                }else {
                    result.append(product.getId());
                }
            }
        }
        return result.toString();
    }

    private void getProductDetailAndProductType(Product products) {
        if (products == null) {
            return;
        }
        getProductDetailAndProductType(Lists.newArrayList(products));
    }

    private void getProductDetailAndProductType(List<Product> products) {
        if (products.size() == 0) {
            return;
        }
        getProductType(products);
        getProductDetail(products);
    }

    private void getProductDetail(List<Product> products) {
        List<Integer> idList = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        Map<Integer, List<ProductDetail>> productDetail = productDetailService.getByProductIds(idList);
        products.forEach(product -> product.setProductDetailList(productDetail.get(product.getId())));
    }

    private void getProductType(List<Product> products) {
        List<Integer> idList = products.stream()
                .map(Product::getTypeId)
                .collect(Collectors.toList());
        Map<Integer, ProductType> result = productTypeService.groupById(idList);
        products.forEach(product -> product.setProductType(result.get(product.getTypeId())));
    }

}
