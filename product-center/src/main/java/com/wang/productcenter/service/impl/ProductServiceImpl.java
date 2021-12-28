package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.productcenter.Redis.RedisService;
import com.wang.productcenter.Util.RedisUtil;
import com.wang.productcenter.dao.ProductDao;
import com.wang.productcenter.entity.BO.Product;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.entity.BO.ProductType;
import com.wang.productcenter.entity.PO.ProductPO;
import com.wang.productcenter.service.IProductDetailService;
import com.wang.productcenter.service.IProductService;
import com.wang.productcenter.service.IProductTypeService;
import org.redisson.api.RLock;
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

    private static final String REDIS_PAGE = "page";

    private static final String REDIS_NAME = "name";

    private static final String REDIS_MAP = ":";

    private static final String REDIS_ID = "id";

    private static final String REDIS_ORDERBY = "orderBy";

    private static final String REDIS_SEPARATE = "-";

    public PageInfo<Product> getAll(Product product) {
        PageInfo<Product> result = null;
        String redisName = productAllGetRedisName(product);
        result = RedisUtil.getByPageInfo(redisName, Product.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    PageUtils.startPage(product);
                    List<ProductPO> poResult = productDao.getAll();
                    List<Product> products = poResult.stream()
                            .map(ProductPO::convertToProduct)
                            .collect(Collectors.toList());
                    getProductType(products);
                    getProductDetail(products);
                    result = PageUtils.getPageInfo(poResult, products);
                    redisService.set(redisName, result);
                } finally {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    @Override
    public Product getById(Product product) {
        Product result = null;
        String redisName = productGetRedisName(product);
        List<String> keys = RedisUtil.keys(redisName);
        List<Product> list = RedisUtil.mget(Product.class, keys);
        if(list != null && list.size() != 0){
            result = list.get(0);
        }
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    list = RedisUtil.mget(Product.class, keys);
                    if(list != null && list.size() != 0){
                        result = list.get(0);
                    }
                    if (result == null) {
                        ProductPO productPO = product.doForward();
                        ProductPO poResult = productDao.getById(productPO);
                        if (poResult == null) {
                            return null;
                        }
                        result = poResult.convertToProduct();
                        getProductDetailAndProductType(result);
                        redisName = productGetRedisName(result);
                        RedisUtil.set(redisName, result);
                    } else {
                        return result;
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    @Override
    public Product getByName(Product product) {
        Product result = null;
        String redisName = productGetRedisName(product);
        List<String> keys = RedisUtil.keys(redisName);
        List<Product> list = RedisUtil.mget(Product.class, keys);
        if(list != null && list.size() != 0){
            result = list.get(0);
        }
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    list = RedisUtil.mget(Product.class, keys);
                    if(list != null && list.size() != 0){
                        result = list.get(0);
                    }
                    if (result == null) {
                        ProductPO productPO = product.doForward();
                        ProductPO poResult = productDao.getByName(productPO);
                        if (poResult == null) {
                            return null;
                        }
                        result = poResult.convertToProduct();
                        getProductDetailAndProductType(result);
                        redisName = productGetRedisName(result);
                        RedisUtil.set(redisName, result);
                    } else {
                        return result;
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    @Override
    // todo 非热点数据没必要使用Redis进行缓存
    //      如果需要缓存这部分数据考虑后期使用ES
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

    /*
        todo:
             删除更新操作保证数据一致性解决方案(未确定是否为最终方案):
                第一次先尝试删除Cache，不管是否成功
                开始对数据库进行写操作，等数据库写操作完毕以后
                开始尝试第二次删除，尽量保证数据一致性（假设第一次删除失败）
                第二次的删除操作如果失败进行三次重试
                如果三次删除都失败则通过MQ转发异步进行删除保证删除操作一定成功
     */
    @Override
    public void remove(Product product) {
        String redisName = productGetRedisName(product);
        String redisNameAll = productAllGetRedisName(product);
        RedisUtil.del(redisName);
        RedisUtil.del(redisNameAll);
        syncRemovePageCache();
        ProductPO productPO = product.doForward();
        // todo 这里只删除了商品表，商品详情和商品分类详情的中间表还未清理
        //      等后期Redis逻辑清晰进行修改
        productDao.remove(productPO);
        redisService.del(redisName);
        redisService.del(redisNameAll);
        asyncRemovePageCache();
    }

    @Override
    public int update(Product product) {
        ProductPO productPO = product.doForward();
        return productDao.update(productPO);
    }

    /**
     *  同步清理分页缓存
     */
    private void syncRemovePageCache(){
        String redisName = REDIS_PREFIX + REDIS_PAGE + ":*";
        List<String> keys = RedisUtil.keys(redisName);
        RedisUtil.del(keys);
    }

    /**
     *  异步清理分页缓存
     */
    private void asyncRemovePageCache(){
        String redisName = REDIS_PREFIX + REDIS_PAGE + ":*";
        List<String> keys = RedisUtil.keys(redisName);
        redisService.del(keys);
    }


    private String productLikeNameGetRedisName(Product product) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (product != null) {
            result.append(REDIS_NAME).append(REDIS_MAP);
            if (product.getProductName() != null) {
                result.append("*")
                        .append(product.getProductName())
                        .append("*");
            } else {
                result.append("*");
            }
            result.append(REDIS_SEPARATE).append(REDIS_ID).append(REDIS_MAP);
            if (product.getId() != null) {
                result.append(product.getId());
            } else {
                result.append("*");
            }
        }
        return result.toString();
    }

    private String productGetRedisName(Product product) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (product != null) {
            result.append(REDIS_NAME).append(REDIS_MAP);
            if (product.getProductName() != null) {
                result.append(product.getProductName());
            } else {
                result.append("*");
            }
            result.append(REDIS_SEPARATE).append(REDIS_ID).append(REDIS_MAP);
            if (product.getId() != null) {
                result.append(product.getId());
            } else {
                result.append("*");
            }
        }
        return result.toString();
    }

    private String productAllGetRedisName(Product product) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (product != null) {
            if (((Page) product).IsPage()) {
                result.append(REDIS_PAGE)
                        .append(REDIS_MAP)
                        .append(product.getPageNum());
                if (product.getOrderBy() != null) {
                    result.append(REDIS_SEPARATE)
                            .append(REDIS_ORDERBY)
                            .append(REDIS_MAP)
                            .append(product.getOrderBy());
                }
            } else {
                if (product.getId() == null) {
                    result.append("ALL");
                } else {
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
