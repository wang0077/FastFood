package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.RedisPageInfo;
import com.wang.fastfood.apicommons.enums.CodeEnum;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.fastfood.apicommons.exception.DeleteException;
import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Util.RedisPageUtil;
import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.productcenter.dao.ProductDetailDao;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.entity.PO.ProductDetailPO;
import com.wang.productcenter.entity.PO.Product_DetailType_Middle;
import com.wang.productcenter.entity.PO.Product_Detail_Middle;
import com.wang.productcenter.service.IDetailTypeService;
import com.wang.productcenter.service.IProductDetailService;
import com.wang.productcenter.service.IProductService;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private IProductService productService;

    @Autowired
    private AsyncRedis redisService;

    private static final String REDIS_PREFIX = "Product-Detail-";

    private static final String REDIS_PAGE_ZSET = "Product-Detail-Zset";

    private static final String REDIS_PAGE_HASH = "Product-Detail-Map";

    @Deprecated
    private static final String ALL = "ALL";

    @Deprecated
    private static final String REDIS_PAGE = "page";

    private static final String REDIS_NAME = "name";

    private static final String REDIS_MAP = ":";

    private static final String REDIS_ID = "id";

    private static final String REDIS_ORDERBY = "orderBy";

    private static final String REDIS_SEPARATE = "-";

    public void flush() {
        List<ProductDetailPO> all = productDetailDao.getAll();
        all.forEach(productPO -> {
            RedisUtil.zadd(REDIS_PAGE_ZSET, productPO.getId(), productPO.getId());
        });
    }

    @Override
    public PageInfo<ProductDetail> getAll(ProductDetail productDetail) {
        PageInfo<ProductDetail> result = null;
        RedisPageInfo<ProductDetail> pageInfo = PageUtils.startRedisPage(productDetail);
        RedisPageUtil.computeRedisPageInfo(pageInfo, REDIS_PAGE_ZSET);
        RedisPageUtil.getPageData(pageInfo, REDIS_PAGE_ZSET, REDIS_PAGE_HASH, ProductDetail.class);
        if (pageInfo.isExistMiss()) {
            RLock lock = RedisUtil.getLock(REDIS_PAGE_HASH);
            if (lock.tryLock()) {
                try {
                    List<ProductDetailPO> poResult = productDetailDao.getByIds(pageInfo.getMissIds());
                    List<ProductDetail> productDetails = poResult
                            .stream()
                            .map(ProductDetailPO::convertToProductDetail)
                            .collect(Collectors.toList());
                    getDetailType(productDetails, true);
                    RedisUtil.hmset(REDIS_PAGE_HASH
                            , productDetails.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.toList())
                            , productDetails);
                    pageInfo.getList().addAll(productDetails);
                    pageInfo.getList().sort(Comparator.comparingInt(ProductDetail::getId));
                } finally {
                    lock.unlock();
                }
            }
        }
        result = PageUtils.getPageInfo(pageInfo);
        return result;
    }

    public int insert(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        ProductDetailPO result_temp = productDetailDao.getByName(productDetailPO);
        if (result_temp != null) {
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        int result = productDetailDao.insert(productDetailPO);
        ProductDetailPO productDetailResult = productDetailDao.getByName(productDetailPO);
        RedisUtil.zadd(REDIS_PAGE_ZSET, productDetailResult.getId(), productDetailResult.getId());
        return result;
    }

    public ProductDetail getByName(ProductDetail productDetail) {
        ProductDetail result = null;
        String redisName = productDetailGetRedisName(productDetail);
        result = RedisUtil.get(redisName, ProductDetail.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    ProductDetailPO productDetailPO = productDetail.doForward();
                    ProductDetailPO poResult = productDetailDao.getByName(productDetailPO);
                    if (poResult == null) {
                        return null;
                    }
                    result = poResult.convertToProductDetail();
                    getDetailType(result);
                    redisName = productDetailGetRedisName(result);
                    redisService.set(redisName, result);
                } finally {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    @Override
    public ProductDetail getById(ProductDetail productDetail) {
        ProductDetail result = null;
        Integer id = productDetail.getId();
        result = RedisUtil.hget(REDIS_PAGE_HASH, String.valueOf(id), ProductDetail.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(REDIS_PAGE_HASH);
            if (lock.tryLock()) {
                try {
                    result = RedisUtil.hget(REDIS_PAGE_HASH, String.valueOf(id), ProductDetail.class);
                    if (result == null) {
                        ProductDetailPO productDetailPO = productDetail.doForward();
                        ProductDetailPO poResult = productDetailDao.getById(productDetailPO);
                        result = poResult.convertToProductDetail();
                        redisService.hset(REDIS_PAGE_HASH, String.valueOf(result.getId()), result);
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
    public void remove(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        Integer productDetailId = productDetail.getId();
        List<Product_Detail_Middle> ProductMiddles = getProductMiddle(productDetailId);

        // 检查删除可行性，是否存在数据依赖性
        if (ProductMiddles.size() > 0) {
            throw new DeleteException("该可选项分类正在被商品使用", CodeEnum.SQL_DELETE_ERROR);
        }
        List<Integer> detailTypeIds = detailTypeService.getIdsByProductDetailId(productDetailId);
        if (detailTypeIds.size() > 0) {
            throw new DeleteException("该可选项分类正在被可选项使用", CodeEnum.SQL_DELETE_ERROR);
        }

        // 判断没有数据依赖性进行删除操作
        productDetailDao.remove(productDetailPO);
        String redisName = productDetailGetRedisName(productDetail);
        List<String> keys = RedisUtil.keys(redisName);
        if (keys == null || keys.size() == 0) {
            return;
        }
        redisService.del(keys);
        redisService.zrem(REDIS_PAGE_ZSET, productDetail.getId());
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
        int result = productDetailDao.update(productDetailPO);

        // 清除可选项分类的相关Hash缓存
        redisService.hdel(REDIS_PAGE_HASH, productDetail.getId());
        Integer productDetailId = productDetail.getId();

        // 清除商品依赖相关Hash缓存
        removeProductCache(productDetailId);

        // 清除可选项依赖相关的Hash缓存
        removeDetailTypeCache(productDetailId);
        return result;
    }

    @Override
    public List<ProductDetail> getByIds(List<Integer> idList) {
        List<ProductDetail> result = null;

        result = RedisUtil.hmget(REDIS_PAGE_HASH
                , idList.stream().map(String::valueOf).collect(Collectors.toList())
                , ProductDetail.class);

        List<Integer> MissId = null;
        if (result != null) {
            if (result.size() != idList.size()) {
                MissId = new ArrayList<>();
                for (Integer integer : idList) {
                    boolean flag = false;
                    for (ProductDetail productDetail : result) {
                        if (productDetail.getId().equals(integer)) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        MissId.add(integer);
                    }
                }
            }
        } else {
            MissId = idList;
        }
        if (MissId != null && MissId.size() > 0) {
            List<ProductDetailPO> poResult = productDetailDao.getByIds(MissId);
            List<ProductDetail> missResult = poResult.stream()
                    .map(ProductDetailPO::convertToProductDetail)
                    .collect(Collectors.toList());
            RedisUtil.hmset(REDIS_PAGE_HASH
                    , missResult.stream().map(productDetail -> String.valueOf(productDetail.getId())).collect(Collectors.toList())
                    , missResult);
            if (result == null) {
                result = missResult;
            } else {
                result.addAll(missResult);
            }
        }
        return result;
    }

    @Override
    public Map<Integer, List<ProductDetail>> getByProductIds(List<Integer> idList) {

        // 通过ProductId查询中间表获取和ProductDetail关联关系
        List<Product_Detail_Middle> productDetailMiddles = getProductMiddle(idList);

        // 通过ProductId查询中间表获取和DetailType关联关系
        List<Product_DetailType_Middle> productDetailTypeMiddles = detailTypeService.getByProductMiddle(idList);

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
        List<ProductDetail> productDetails = getByIds(productDetailIds);

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

        Map<Integer, List<ProductDetail>> productDetailMap = productDetailMiddles.stream()
                .collect(Collectors.toMap(Product_Detail_Middle::getProductId
                        , middle -> productDetails.stream()
                                .filter(productDetail -> productDetail.getId().equals(middle.getProductDetailId()))
                                .map(ProductDetail::clone)
                                .collect(Collectors.toList())
                        , (List<ProductDetail> v1, List<ProductDetail> v2) -> {
                            v1.addAll(v2);
                            return v1;
                        }));
        idList.forEach(productId -> {
            List<DetailType> detailTypeList = productMap.get(productId);
            List<ProductDetail> details = productDetailMap.get(productId);
            details.forEach(detail -> {
                detail.setDetailTypeList(detailTypeList
                        .stream()
                        .filter(detailType -> detailType.getProductDetailId().equals(detail.getId()))
                        .collect(Collectors.toList()));
            });
        });
        return productDetailMap;
    }

    /**
     * 关联商品和商品详细的关联关系
     */
    @Override
    public void productAssociationDetail(int productId, List<Integer> detailId) {
        // todo 后期这段要改成批处理
        detailId.forEach(id -> {
            productDetailDao.productAssociationDetail(productId, id);
        });
    }

    /**
     * 删除商品和商品详细的关联关系
     */
    @Override
    public void productDisconnectDetail(int productId, List<Integer> detailId) {
        // todo 后期这段要改成批处理
        detailId.forEach(id -> {
            productDetailDao.productDisconnectDetail(productId, id);
        });
    }

    @Override
    public List<Integer> getProductDetailIdsByProductId(int productId) {
        List<Product_Detail_Middle> Middle = getProductMiddle(productId);
        return Middle.stream()
                .map(Product_Detail_Middle::getProductDetailId)
                .collect(Collectors.toList());
    }

    /**
     * 删除可选项分类的Hash缓存
     */
    public int removeProductDetailCache(Integer productDetailId) {
        return removeProductDetailCache(Collections.singletonList(productDetailId));
    }

    /**
     * 删除可选项分类的Hash缓存
     */
    public int removeProductDetailCache(List<Integer> productDetailIds) {
        String result = redisService.hdel(REDIS_PAGE_HASH, productDetailIds);
        return Integer.parseInt(result);
    }

    private void removeProductCache(Integer productDetailId) {
        List<Integer> productIds = productService.getProductIdsByDetail(productDetailId);
        productService.removeProductCacheByProductId(productIds);
    }

    private void removeDetailTypeCache(Integer productDetailId) {
        List<Integer> detailTypeIds = detailTypeService.getIdsByProductDetailId(productDetailId);
        detailTypeService.removeDetailTypeCacheByDetailTypeId(detailTypeIds);
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
        String redisName = productDetailAllGetRedisName();
        RedisUtil.del(redisName);
    }

    @Deprecated
    private String productDetailAllGetRedisName() {
        return REDIS_PREFIX + ALL;
    }

    @Deprecated
    private List<String> productDetailIdsGetRedisName(List<Integer> ids) {
        List<String> redisNameList = new ArrayList<>();
        String result = REDIS_PREFIX +
                REDIS_NAME +
                REDIS_MAP +
                "*" +
                REDIS_ID +
                REDIS_MAP;
        ids.forEach(id -> redisNameList.add(result + id));
        return redisNameList;
    }

    @Deprecated
    private String productDetailLikeNameGetRedisName(ProductDetail productDetail) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (productDetail != null) {
            result.append(REDIS_NAME).append(REDIS_MAP);
            if (productDetail.getProductDetailName() != null) {
                result.append("*")
                        .append(productDetail.getProductDetailName())
                        .append("*");
            } else {
                result.append("*");
            }
            result.append(REDIS_SEPARATE).append(REDIS_ID).append(REDIS_MAP);
            if (productDetail.getId() != null) {
                result.append(productDetail.getId());
            } else {
                result.append("*");
            }
        }
        return result.toString();
    }

    private String productDetailGetRedisName(ProductDetail productDetail) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (productDetail != null) {
            result.append(REDIS_NAME).append(REDIS_MAP);
            if (productDetail.getProductDetailName() != null) {
                result.append(productDetail.getProductDetailName());
            } else {
                result.append("*");
            }
            result.append(REDIS_SEPARATE).append(REDIS_ID).append(REDIS_MAP);
            if (productDetail.getId() != null) {
                result.append(productDetail.getId());
            } else {
                result.append("*");
            }
        }
        return result.toString();
    }

    @Deprecated
    private String productDetailAllGetRedisName(ProductDetail productDetail) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (productDetail != null) {
            if (((Page) productDetail).IsPage()) {
                result.append(REDIS_PAGE)
                        .append(REDIS_MAP)
                        .append(productDetail.getPageNum());
                if (productDetail.getOrderBy() != null) {
                    result.append(REDIS_SEPARATE)
                            .append(REDIS_ORDERBY)
                            .append(REDIS_MAP)
                            .append(productDetail.getOrderBy());
                }
            } else {
                if (productDetail.getId() == null) {
                    result.append("ALL");
                } else {
                    result.append(productDetail.getId());
                }
            }
        }
        return result.toString();
    }

    private List<Product_Detail_Middle> getProductMiddle(int id) {
        return getProductMiddle(Collections.singletonList(id));
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
