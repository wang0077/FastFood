package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.productcenter.Redis.RedisService;
import com.wang.productcenter.Util.RedisUtil;
import com.wang.productcenter.dao.ProductDetailDao;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.entity.PO.ProductDetailPO;
import com.wang.productcenter.entity.PO.Product_DetailType_Middle;
import com.wang.productcenter.entity.PO.Product_Detail_Middle;
import com.wang.productcenter.service.IDetailTypeService;
import com.wang.productcenter.service.IProductDetailService;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private RedisService redisService;

    private static final String REDIS_PREFIX = "Product-Detail-";

    private static final String REDIS_PAGE = "page";

    private static final String REDIS_NAME = "name";

    private static final String REDIS_MAP = ":";

    private static final String REDIS_ID = "id";

    private static final String REDIS_ORDERBY = "orderBy";

    private static final String REDIS_SEPARATE = "-";


    @Override
    public PageInfo<ProductDetail> getAll(ProductDetail productDetail) {
        PageInfo<ProductDetail> result = null;
        String redisName = productDetailAllGetRedisName(productDetail);
        result = RedisUtil.getByPageInfo(redisName, ProductDetail.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    PageUtils.startPage(productDetail);
                    List<ProductDetailPO> poResult = productDetailDao.getAll();
                    List<ProductDetail> productDetails = poResult
                            .stream()
                            .map(ProductDetailPO::convertToProductDetail)
                            .collect(Collectors.toList());
                    getDetailType(productDetails, productDetail.isDetail());
                    result = PageUtils.getPageInfo(poResult, productDetails);
                    if (result != null) {
                        redisService.set(redisName, result);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    public int insert(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        ProductDetailPO result_temp = productDetailDao.getByName(productDetailPO);
        if (result_temp != null) {
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        return productDetailDao.insert(productDetailPO);
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
        String redisName = productDetailGetRedisName(productDetail);
        result = RedisUtil.get(redisName, ProductDetail.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    ProductDetailPO productDetailPO = productDetail.doForward();
                    ProductDetailPO poResult = productDetailDao.getById(productDetailPO);
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
    public void remove(ProductDetail productDetail) {
        ProductDetailPO productDetailPO = productDetail.doForward();
        productDetailDao.remove(productDetailPO);
        String redisName = productDetailGetRedisName(productDetail);
        List<String> keys = RedisUtil.keys(redisName);
        if (keys == null || keys.size() == 0) {
            return;
        }
        redisService.del(keys);
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
        syncRemovePageCache();
        ProductDetailPO productDetailPO = productDetail.doForward();
        int result = productDetailDao.update(productDetailPO);
        asyncRemovePageCache();
        return result;
    }

    @Override
    public List<ProductDetail> getByIds(List<Integer> idList) {
        List<ProductDetail> result = null;
        List<String> redisNameList = productDetailIdsGetRedisName(idList);
        List<String> keys = RedisUtil.keys(redisNameList);
        if (keys != null && keys.size() > 0) {
            result = RedisUtil.mget(ProductDetail.class, keys);
        }
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
        if(MissId != null && MissId.size() > 0){
            List<ProductDetailPO> poResult = productDetailDao.getByIds(MissId);
            result = poResult.stream()
                    .map(ProductDetailPO::convertToProductDetail)
                    .collect(Collectors.toList());
            redisNameList = new ArrayList<>();
            for (ProductDetail productDetail : result) {
                redisNameList.add(productDetailGetRedisName(productDetail));
            }
            redisService.mset(redisNameList,result);
        }
        return result;
    }

    @Override
    public Map<Integer, List<ProductDetail>> getByProductIds(List<Integer> idList) {

        // 通过ProductId查询中间表获取和ProductDetail关联关系
        List<Product_Detail_Middle> productDetailMiddles = getProductMiddle(idList);

        // 通过ProductId查询中间表获取和DetailType关联关系
        List<Product_DetailType_Middle> productDetailTypeMiddles = detailTypeService.getByProductId(idList);

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
        // 通过Product和DetailType映射关系再和Product和ProductDetail关系
        // 整合出Product对应的ProductDetail封装和Product对应的DetailType
        return productDetailMiddles.stream()
                .collect(Collectors.toMap(Product_Detail_Middle::getProductId
                        , middle -> productDetails.stream()
                                .filter(productDetail -> {
                                    Integer productId = middle.getProductId();
                                    List<DetailType> detailTypeList = productMap.get(productId);
                                    List<DetailType> list = detailTypeList.stream()
                                            .filter(detailType -> detailType.getProductDetailId().equals(productDetail.getId()))
                                            .collect(Collectors.toList());
                                    if (list.size() == 0) {
                                        return false;
                                    }
                                    productDetail.setDetailTypeList(list);
                                    return true;
                                })
                                .collect(Collectors.toList())
                        , (List<ProductDetail> v1, List<ProductDetail> v2) -> v1));
    }

    /**
     * 同步清理分页缓存
     */
    private void syncRemovePageCache() {
        String redisName = REDIS_PREFIX + REDIS_PAGE + ":*";
        List<String> keys = RedisUtil.keys(redisName);
        RedisUtil.del(keys);
    }

    /**
     * 异步清理分页缓存
     */
    private void asyncRemovePageCache() {
        String redisName = REDIS_PREFIX + REDIS_PAGE + ":*";
        List<String> keys = RedisUtil.keys(redisName);
        redisService.del(keys);
    }

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
