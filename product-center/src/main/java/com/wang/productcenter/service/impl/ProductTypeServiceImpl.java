package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.RedisPageInfo;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.fastfood.apicommons.exception.DeleteException;
import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Util.RedisPageUtil;
import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.productcenter.dao.ProductTypeDao;
import com.wang.productcenter.entity.BO.Product;
import com.wang.productcenter.entity.BO.ProductType;
import com.wang.productcenter.entity.PO.ProductTypePO;
import com.wang.productcenter.service.IProductService;
import com.wang.productcenter.service.IProductTypeService;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

    @Autowired
    private IProductService productService;

    @Autowired
    private AsyncRedis redisService;

    private static final String REDIS_PREFIX = "Product-Type-";

    private static final String REDIS_PAGE_ZSET = "Product-Type-Zset";

    private static final String REDIS_PAGE_HASH = "Product-Type-Map";

    @Deprecated
    private static final String ALL = "ALL";

    @Deprecated
    private static final String REDIS_PAGE = "page";

    private static final String REDIS_NAME = "name";

    private static final String REDIS_MAP = ":";

    private static final String REDIS_ID = "id";

    private static final String REDIS_ORDERBY = "orderBy";

    private static final String REDIS_SEPARATE = "-";

    public void flush(){
        List<ProductTypePO> all = productTypeDao.getAll();
        all.forEach(productTypePO -> {
            RedisUtil.zadd(REDIS_PAGE_ZSET,productTypePO.getId(),productTypePO.getId());
        });
    }

    @Override
    public PageInfo<ProductType> getAll(ProductType productType) {

        PageInfo<ProductType> result = null;
        RedisPageInfo<ProductType> pageInfo = PageUtils.startRedisPage(productType);
        RedisPageUtil.computeRedisPageInfo(pageInfo,REDIS_PAGE_ZSET);
        RedisPageUtil.getPageData(pageInfo,REDIS_PAGE_ZSET,REDIS_PAGE_HASH,ProductType.class);
        if (pageInfo.isExistMiss()) {
            RLock lock = RedisUtil.getLock(REDIS_PAGE_HASH);
            if (lock.tryLock()) {
                try {
                    List<ProductTypePO> missResult = productTypeDao.groupById(pageInfo.getMissIds());
                    List<ProductType> productTypeList = missResult.stream()
                            .map(ProductTypePO::convertToProductType)
                            .collect(Collectors.toList());
                    RedisUtil.hmset(REDIS_PAGE_HASH
                            ,productTypeList.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.toList())
                            ,productTypeList);
                    pageInfo.getList().addAll(productTypeList);
                    pageInfo.getList().sort(Comparator.comparingInt(ProductType::getId));
                } finally {
                    lock.unlock();
                }
            }
        }
        result = PageUtils.getPageInfo(pageInfo);
        return result;
    }

    @Override
    public int insert(ProductType productType) {
        ProductTypePO productTypePO = productType.doForward();
        ProductTypePO result = productTypeDao.getByName(productTypePO);
        if (result != null) {
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        int count = productTypeDao.insert(productTypePO);
        result = productTypeDao.getByName(productTypePO);
        // 维护分页Zset
        RedisUtil.zadd(REDIS_PAGE_ZSET,result.getId(),result.getId());
        return count;
    }

    @Override
    public ProductType getById(ProductType productType) {
        ProductType result = null;
        Integer id = productType.getId();
        result = RedisUtil.hget(REDIS_PAGE_HASH,String.valueOf(id),ProductType.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(REDIS_PAGE_HASH);
            if (lock.tryLock()) {
                try {
                    result = RedisUtil.hget(REDIS_PAGE_HASH,String.valueOf(id),ProductType.class);
                    if (result == null) {
                        ProductTypePO productTypePO = productType.doForward();
                        ProductTypePO poResult = productTypeDao.getById(productTypePO);
                        result = poResult.convertToProductType();
                        redisService.hset(REDIS_PAGE_HASH,String.valueOf(result.getId()),result);
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

    public PageInfo<ProductType> getLikeName(ProductType productType) {
        ProductTypePO productTypePO = productType.doForward();
        List<ProductTypePO> result = productTypeDao.getLikeName(productTypePO);
        return PageUtils.getPageInfo(result, result
                .stream()
                .map(ProductTypePO::convertToProductType)
                .collect(Collectors.toList()));
    }

    public ProductType getByName(ProductType productType) {
        ProductType result = null;
        String redisName = productTypeGetRedisName(productType);
        List<String> keys = RedisUtil.keys(redisName);
        result = RedisUtil.mget(ProductType.class, keys).get(0);
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    result = RedisUtil.mget(ProductType.class, keys).get(0);
                    if (result == null) {
                        ProductTypePO productTypePO = productType.doForward();
                        ProductTypePO poResult = productTypeDao.getByName(productTypePO);
                        if (poResult != null) {
                            result = poResult.convertToProductType();
                            redisName = productTypeGetRedisName(result);
                            redisService.set(redisName, result);
                        }
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

    public void removeType(ProductType productType) {
        ProductTypePO productTypePO = productType.doForward();

        Integer id = productType.getId();
        // 检查是否有商品正在使用这个数据
        List<Product> checkList = productService.getProductByTypeId(id);
        if(checkList.size() > 0){
            throw new DeleteException("此分类正在被商品正在使用",CodeEnum.SQL_DELETE_ERROR);
        }

        // 删除数据没有存在依赖性，进行删除
        productTypeDao.remove(productTypePO);
        redisService.zrem(REDIS_PAGE_ZSET,productType.getId());
        String redisName = productTypeGetRedisName(productType);
        redisService.del(redisName);
    }

    public int updateType(ProductType productType) {
        ProductTypePO productTypePO = productType.doForward();
        int result = productTypeDao.update(productTypePO);
        redisService.hdel(REDIS_PAGE_HASH,productType.getId());
        String redisName = productTypeGetRedisName(productType);
        redisService.del(redisName);

        // 清除商品依赖相关Hash缓存
        removeProductCache(productType);
        return result;
    }

    @Override
    public Map<Integer, ProductType> groupById(List<Integer> idList) {
        List<ProductTypePO> result = getByIdList(idList);
        List<ProductType> productTypes = result.stream()
                .map(ProductTypePO::convertToProductType)
                .collect(Collectors.toList());
        return productTypes.stream().collect(Collectors.toMap(ProductType::getId, productType -> productType));
    }

    private void removeProductCache(ProductType productType) {
        List<Integer> productIds = productService.getProductIdsByTypeId(productType.getId());
        productService.removeProductCacheByProductId(productIds);
    }

    @Deprecated
    private void removeAllCache(){
        String redisName = productTypeAllGetRedisName();
        RedisUtil.del(redisName);
    }

    /**
     * 同步清理分页缓存
     */
    @Deprecated
    private void syncRemovePageCache() {
        String redisName = REDIS_PREFIX + REDIS_PAGE + ":*";
        List<String> keys = RedisUtil.keys(redisName);
        if (keys == null || keys.size() == 0) {
            return;
        }
        RedisUtil.del(keys);
    }

    /**
     * 异步清理分页缓存
     */
    @Deprecated
    private void asyncRemovePageCache() {
        String redisName = REDIS_PREFIX + REDIS_PAGE + ":*";
        List<String> keys = RedisUtil.keys(redisName);
        if (keys == null || keys.size() == 0) {
            return;
        }
        redisService.del(keys);
    }

    @Deprecated
    private String productTypeAllGetRedisName(){
        return REDIS_PREFIX + ALL;
    }

    @Deprecated
    private String productTypeLikeNameGetRedisName(ProductType type) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (type != null) {
            result.append(REDIS_NAME).append(REDIS_MAP);
            if (type.getName() != null) {
                result.append("*")
                        .append(type.getName())
                        .append("*");
            } else {
                result.append("*");
            }
            result.append(REDIS_SEPARATE).append(REDIS_ID).append(REDIS_MAP);
            if (type.getId() != null) {
                result.append(type.getId());
            } else {
                result.append("*");
            }
        }
        return result.toString();
    }

    private String productTypeGetRedisName(ProductType type) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (type != null) {
            result.append(REDIS_NAME).append(REDIS_MAP);
            if (type.getName() != null) {
                result.append(type.getName());
            } else {
                result.append("*");
            }
            result.append(REDIS_SEPARATE).append(REDIS_ID).append(REDIS_MAP);
            if (type.getId() != null) {
                result.append(type.getId());
            } else {
                result.append("*");
            }
        }
        return result.toString();
    }

    @Deprecated
    private String productTypeAllGetRedisName(ProductType type) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (type != null) {
            if (((Page) type).IsPage()) {
                result.append(REDIS_PAGE)
                        .append(REDIS_MAP)
                        .append(type.getPageNum());
                if (type.getOrderBy() != null) {
                    result.append(REDIS_SEPARATE)
                            .append(REDIS_ORDERBY)
                            .append(REDIS_MAP)
                            .append(type.getOrderBy());
                }
            } else {
                if (type.getId() == null) {
                    result.append("ALL");
                } else {
                    result.append(type.getId());
                }
            }
        }
        return result.toString();
    }

    private List<ProductTypePO> getByIdList(List<Integer> idList) {
        return productTypeDao.groupById(idList);
    }

}
