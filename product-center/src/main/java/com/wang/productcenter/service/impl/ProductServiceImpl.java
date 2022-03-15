package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.RedisPageInfo;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Util.RedisPageUtil;
import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.productcenter.dao.ProductDao;
import com.wang.productcenter.entity.BO.Product;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.entity.BO.ProductType;
import com.wang.productcenter.entity.PO.ProductPO;
import com.wang.productcenter.service.IDetailTypeService;
import com.wang.productcenter.service.IProductDetailService;
import com.wang.productcenter.service.IProductService;
import com.wang.productcenter.service.IProductTypeService;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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
    private IDetailTypeService detailTypeService;

    @Autowired
    private AsyncRedis redisService;

    private static final String REDIS_PREFIX = "Product-";

    private static final String REDIS_PAGE_ZSET = "Product-Zset";

    private static final String REDIS_PAGE_HASH = "Product-Map";

    private static final String ALL = "ALL";

    private static final String REDIS_PAGE = "page";

    private static final String REDIS_NAME = "name";

    private static final String REDIS_MAP = ":";

    private static final String REDIS_ID = "id";

    private static final String REDIS_ORDERBY = "orderBy";

    private static final String REDIS_SEPARATE = "-";

    public void flush(){
        List<ProductPO> all = productDao.getAll();
        all.forEach(productPO -> {
            RedisUtil.zadd(REDIS_PAGE_ZSET,productPO.getId(),productPO.getId());
        });
    }

    public PageInfo<Product> getAll(Product product) {
        PageInfo<Product> result = null;

        RedisPageInfo<Product> pageInfo = PageUtils.startRedisPage(product);
        RedisPageUtil.computeRedisPageInfo(pageInfo,REDIS_PAGE_ZSET);
        RedisPageUtil.getPageData(pageInfo,REDIS_PAGE_ZSET,REDIS_PAGE_HASH,Product.class);

        if (pageInfo.isExistMiss()) {
            RLock lock = RedisUtil.getLock(REDIS_PAGE_HASH);
            if (lock.tryLock()) {
                try {
                    // todo 没有双重校验，可能存在线程不安全
                    List<ProductPO> missResult = productDao.getByIds(pageInfo.getMissIds());
                    List<Product> products = missResult.stream()
                            .map(ProductPO::convertToProduct)
                            .collect(Collectors.toList());
                    // todo 同步添加缓存没有保障机制
                    RedisUtil.hmset(REDIS_PAGE_HASH
                            ,products.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.toList())
                            ,products);
                    pageInfo.getList().addAll(products);
                    pageInfo.getList().sort(Comparator.comparingInt(Product::getId));
                } finally {
                    lock.unlock();
                }
            }
        }
        result = PageUtils.getPageInfo(pageInfo);
        return result;
    }

    @Override
    public Product getById(Product product) {
        Product result = null;
        Integer id = product.getId();
        result = RedisUtil.hget(REDIS_PAGE_HASH,String.valueOf(id),Product.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(REDIS_PAGE_HASH);
            if (lock.tryLock()) {
                try {
                    result = RedisUtil.hget(REDIS_PAGE_HASH,String.valueOf(id),Product.class);
                    if(result == null){
                        ProductPO productPO = product.doForward();
                        ProductPO poResult = productDao.getById(productPO);
                        result = poResult.convertToProduct();
                        redisService.hset(REDIS_PAGE_HASH,String.valueOf(result.getId()),result);
                    }else{
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
        if (list != null && list.size() != 0) {
            result = list.get(0);
        }
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    list = RedisUtil.mget(Product.class, keys);
                    if (list != null && list.size() != 0) {
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
        ProductPO productPO = product.doForward();
        // todo 这里只删除了商品表，商品详情和商品分类详情的中间表还未清理
        //      等后期Redis逻辑清晰进行修改
        productDao.remove(productPO);
        redisService.hdel(REDIS_PAGE_ZSET,product.getId());
        redisService.del(redisName);
    }

    @Override
    public int update(Product product) {
        ProductPO productPO = product.doForward();
        productDao.update(productPO);
        int productId = productPO.getId();

        // 获取更新的Detail数据
        List<Integer> curDetailIds = product.getProductDetailList()
                .stream()
                .map(ProductDetail::getId)
                .collect(Collectors.toList());

        // 获取更新的DetailTypeIds
        List<Integer> curDetailTypeIds = new ArrayList<>();
        product.getProductDetailList().forEach(productDetail -> {
            productDetail.getDetailTypeList().forEach(detailType -> {
                curDetailTypeIds.add(detailType.getId());
            });
        });

        // 获取表中旧的Detail数据
        List<Integer> oldDetailIds = productDetailService.getProductDetailIdsByProductId(productId);
        List<Integer> oldDetailTypeIds = detailTypeService.getDetailTypeIdsByProductId(productId);

        // 处理新旧Detail数据
        // 取交集获取不需要变更的数据
        List<Integer> noChangeDetailIds = intersection(curDetailIds, oldDetailIds);
        List<Integer> noChangeDetailTypeIds = intersection(curDetailTypeIds, oldDetailTypeIds);

        // 取一次差集获取需要删除的数据
        List<Integer> deleteDetailIds = leftDifference(oldDetailIds, noChangeDetailIds);
        List<Integer> deleteDetailTypeIds = leftDifference(oldDetailTypeIds, noChangeDetailTypeIds);

        // 新旧数据取一次差集为新增数据做准备
        List<Integer> differenceDetailIds = symmetricDifference(oldDetailIds, curDetailIds);
        List<Integer> differenceDetailTypeIds = symmetricDifference(oldDetailTypeIds, curDetailTypeIds);

        // 将差集数据与表中数据再做一次差集
        List<Integer> addDetailIds = leftDifference(differenceDetailIds, oldDetailIds);
        List<Integer> addDetailTypeIds = leftDifference(differenceDetailTypeIds, oldDetailTypeIds);

        // 更新数据
        productDetailService.productAssociationDetail(productId, addDetailIds);
        detailTypeService.productAssociationDetailType(productId, addDetailTypeIds);

        // 删除失效数据
        productDetailService.productDisconnectDetail(productId, deleteDetailIds);
        detailTypeService.productDisconnectDetailType(productId, deleteDetailTypeIds);

        redisService.hdel(REDIS_PAGE_HASH,product.getId());
        return 1;
    }

    @Override
    public int insert(Product product) {
        ProductPO productPO = product.doForward();

        ProductPO result_temp = productDao.getByName(productPO);

        if (result_temp != null) {
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }

        productDao.insert(productPO);

        int productId = productDao.getProductIdByName(productPO.getProductName());

        // 关联商品和商品可选项
        List<Integer> detailId = product.getProductDetailList()
                .stream()
                .map(ProductDetail::getId)
                .collect(Collectors.toList());
        productDetailService.productAssociationDetail(productId, detailId);

        // 关联商品和商品可选项详情
        ArrayList<Integer> detailTypeIds = new ArrayList<>();
        product.getProductDetailList().forEach(productDetail -> {
            productDetail.getDetailTypeList().forEach(detailType -> {
                detailTypeIds.add(detailType.getId());
            });
        });
        detailTypeService.productAssociationDetailType(productId, detailTypeIds);
        RedisUtil.zadd(REDIS_PAGE_ZSET,product.getId(),product.getId());
        // todo 后期事务上来以后要修改
        return 1;
    }

    @Override
    public List<Product> getProductByTypeId(Integer id) {
        List<ProductPO> productPOList = productDao.getProductByTypeId(id);
        return productPOList.stream()
                .map(ProductPO::convertToProduct)
                .collect(Collectors.toList());
    }

    /**
     * 获取交集
     */
    private List<Integer> intersection(List<Integer> curList, List<Integer> oldList) {
        return curList.stream()
                .filter(oldList::contains)
                .collect(Collectors.toList());
    }

    /**
     *  获取对称差集
     */
    private List<Integer> symmetricDifference(List<Integer> curList, List<Integer> oldList) {
        List<Integer> tempList = curList.stream()
                .filter(integer -> !oldList.contains(integer))
                .collect(Collectors.toList());
        oldList.forEach(integer -> {
                    if(!curList.contains(integer)){
                        tempList.add(integer);
                    }
                });
        return tempList;
    }

    /**
     *  获取左边集合的差集
     */
    private List<Integer> leftDifference(List<Integer> left, List<Integer> right) {
        return left.stream()
                .filter(integer -> !right.contains(integer))
                .collect(Collectors.toList());
    }


    /**
     * 同步清理分页缓存
     */
    @Deprecated
    private void syncRemovePageCache() {
        String redisName = REDIS_PREFIX + REDIS_PAGE + ":*";
        List<String> keys = RedisUtil.keys(redisName);
        RedisUtil.del(keys);
    }

    /**
     * 异步清理分页缓存
     */
    @Deprecated
    private void asyncRemovePageCache() {
        String redisName = REDIS_PREFIX + REDIS_PAGE + ":*";
        List<String> keys = RedisUtil.keys(redisName);
        redisService.del(keys);
    }

    @Deprecated
    private void removeAllCache() {
        String redisName = productAllGetRedisName();
        RedisUtil.del(redisName);
    }

    @Deprecated
    private String productAllGetRedisName() {
        return REDIS_PREFIX + ALL;
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

    @Deprecated
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
