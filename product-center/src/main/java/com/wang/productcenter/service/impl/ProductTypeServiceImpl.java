package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.productcenter.dao.ProductTypeDao;
import com.wang.productcenter.entity.BO.ProductType;
import com.wang.productcenter.entity.PO.ProductTypePO;
import com.wang.productcenter.service.IProductTypeService;
import org.redisson.api.RLock;
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

    @Autowired
    private AsyncRedis redisService;

    private static final String REDIS_PREFIX = "Product-Type-";

    private static final String ALL = "ALL";

    private static final String REDIS_PAGE = "page";

    private static final String REDIS_NAME = "name";

    private static final String REDIS_MAP = ":";

    private static final String REDIS_ID = "id";

    private static final String REDIS_ORDERBY = "orderBy";

    private static final String REDIS_SEPARATE = "-";

    @Override
    public PageInfo<ProductType> getAll(ProductType productType) {

        PageInfo<ProductType> result = null;
        String redisName = productTypeAllGetRedisName(productType);
        result = RedisUtil.getByPageInfo(redisName, ProductType.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    PageUtils.startPage(productType);
                    List<ProductTypePO> poResult = productTypeDao.getAll();
                    result = PageUtils.getPageInfo(poResult, poResult
                            .stream()
                            .map(ProductTypePO::convertToProductType)
                            .collect(Collectors.toList()));
                    redisService.set(redisName, result);
                } finally {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    @Override
    public int insert(ProductType productType) {
        syncRemovePageCache();
        ProductTypePO productTypePO = productType.doForward();
        ProductTypePO result = productTypeDao.getByName(productTypePO);
        if (result != null) {
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        int count = productTypeDao.insert(productTypePO);
        asyncRemovePageCache();
        removeAllCache();
        return count;
    }

    @Override
    public ProductType getById(ProductType productType) {
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
                        ProductTypePO poResult = productTypeDao.getById(productTypePO);
                        result = poResult.convertToProductType();
                        redisName = productTypeGetRedisName(result);
                        redisService.set(redisName, result);
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
        syncRemovePageCache();
        ProductTypePO productTypePO = productType.doForward();
        productTypeDao.remove(productTypePO);
        asyncRemovePageCache();
        removeAllCache();
    }

    public int updateType(ProductType productType) {
        syncRemovePageCache();
        ProductTypePO productTypePO = productType.doForward();
        int result = productTypeDao.update(productTypePO);
        asyncRemovePageCache();
        removeAllCache();
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

    private void removeAllCache(){
        String redisName = productTypeAllGetRedisName();
        RedisUtil.del(redisName);
    }

    /**
     * 同步清理分页缓存
     */
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
    private void asyncRemovePageCache() {
        String redisName = REDIS_PREFIX + REDIS_PAGE + ":*";
        List<String> keys = RedisUtil.keys(redisName);
        if (keys == null || keys.size() == 0) {
            return;
        }
        redisService.del(keys);
    }

    private String productTypeAllGetRedisName(){
        return REDIS_PREFIX + ALL;
    }

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
