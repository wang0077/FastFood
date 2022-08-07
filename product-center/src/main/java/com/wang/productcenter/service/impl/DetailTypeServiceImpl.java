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
import com.wang.productcenter.dao.DetailTypeDao;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.entity.PO.DetailTypePO;
import com.wang.productcenter.entity.PO.Product_DetailType_Middle;
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
 * @Date: 2021/12/2 02:11
 * @Description:
 */

@Service
public class DetailTypeServiceImpl implements IDetailTypeService {

    @Autowired
    private DetailTypeDao detailTypeDao;

    @Autowired
    private IProductDetailService productDetailService;

    @Autowired
    private IProductService productService;

    @Autowired
    private AsyncRedis redisService;

    private static final String REDIS_PREFIX = "Detail-Type-";

    private static final String REDIS_PAGE_ZSET = "Detail-Type-Zset";

    private static final String REDIS_PAGE_HASH = "Detail-Type-Map";

    private static final String ALL = "ALL";

    private static final String REDIS_PAGE = "page";

    private static final String REDIS_NAME = "name";

    private static final String REDIS_MAP = ":";

    private static final String REDIS_ID = "id";

    private static final String REDIS_ORDERBY = "orderBy";

    private static final String REDIS_SEPARATE = "-";

    public void flush(){
        if(true){
            throw new NullPointerException();
        }
//        List<DetailTypePO> all = detailTypeDao.getAll();
//        all.forEach(productPO -> {
//            RedisUtil.zadd(REDIS_PAGE_ZSET,productPO.getId(),productPO.getId());
//        });
    }

    public int insert(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        // 商品详情ID未传入或商品详情ID在对应表不存在返回插入失败
        if (detailTypePO.getProductDetailId() == null || validityProductDetailId(detailType)) {
            return SqlResultEnum.ERROR_INSERT.getValue();
        }
        DetailTypePO checkResult = detailTypeDao.getByName(detailTypePO);
        if (checkResult != null) {
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        int result = detailTypeDao.insert(detailTypePO);
        checkResult = detailTypeDao.getByName(detailTypePO);
        RedisUtil.zadd(REDIS_PAGE_ZSET,checkResult.getId(),checkResult.getId());
        removeProductDetailCache(detailType.getProductDetailId());
        return result;
    }

    @Override
    public PageInfo<DetailType> getAll(DetailType detailType) {
        PageInfo<DetailType> result = null;
        RedisPageInfo<DetailType> pageInfo = PageUtils.startRedisPage(detailType);
        RedisPageUtil.computeRedisPageInfo(pageInfo,REDIS_PAGE_ZSET);
        RedisPageUtil.getPageData(pageInfo,REDIS_PAGE_ZSET,REDIS_PAGE_HASH,DetailType.class);
        if (pageInfo.isExistMiss()) {
            RLock lock = RedisUtil.getLock(REDIS_PAGE_HASH);
            if (lock.tryLock()) {
                try {
                    List<DetailTypePO> poResult = detailTypeDao.getByIds(pageInfo.getMissIds());
                    List<DetailType> detailTypeList = poResult.stream()
                            .map(DetailTypePO::convertToDetailType)
                            .collect(Collectors.toList());
                    getProductDetail(detailTypeList);
                    RedisUtil.hmset(REDIS_PAGE_HASH
                            ,detailTypeList.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.toList())
                            ,detailTypeList);
                    pageInfo.getList().addAll(detailTypeList);
                    pageInfo.getList().sort(Comparator.comparingInt(DetailType::getId));
                } finally {
                    lock.unlock();
                }
            }
        }
        result = PageUtils.getPageInfo(pageInfo);
        return result;
    }

    @Override
    public DetailType getById(DetailType detailType) {
        DetailType result = null;
        Integer id = detailType.getId();
        result = RedisUtil.hget(REDIS_PAGE_HASH,String.valueOf(id), DetailType.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(REDIS_PAGE_HASH);
            if (lock.tryLock()) {
                try {
                    result = RedisUtil.hget(REDIS_PAGE_HASH,String.valueOf(id),DetailType.class);
                    if (result != null) {
                        DetailTypePO detailTypePO = detailType.doForward();
                        DetailTypePO poResult = detailTypeDao.getById(detailTypePO);
                        result = poResult.convertToDetailType();
                        redisService.hset(REDIS_PAGE_HASH,String.valueOf(result.getId()),result);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    @Override
    public DetailType getByName(DetailType detailType) {
        DetailType result = null;
        String redisName = DetailTypeGetRedisName(detailType);
        result = RedisUtil.get(redisName, DetailType.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    DetailTypePO detailTypePO = detailType.doForward();
                    DetailTypePO poResult = detailTypeDao.getByName(detailTypePO);
                    if (poResult != null) {
                        result = poResult.convertToDetailType();
                        redisName = DetailTypeGetRedisName(detailType);
                        redisService.set(redisName, result);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    @Override
    public PageInfo<DetailType> getLikeName(DetailType detailType) {
        PageUtils.startPage(detailType);
        DetailTypePO detailTypePO = detailType.doForward();
        List<DetailTypePO> result = detailTypeDao.getLikeName(detailTypePO);
        return PageUtils.getPageInfo(result, result
                .stream()
                .map(DetailTypePO::convertToDetailType)
                .collect(Collectors.toList()));
    }

    @Override
    public void remove(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        detailTypePO = detailTypeDao.getById(detailTypePO);

        // 检查该数据的依赖情况

        List<Product_DetailType_Middle> middles =
                productService.getProductByDetailTypeId(Collections.singletonList(detailTypePO.getId()));
        if(middles.size() > 0){
            throw new DeleteException("该可选项正在被商品正在使用", CodeEnum.SQL_DELETE_ERROR);
        }

        // 数据无依赖情况进行删除
        detailTypeDao.remove(detailTypePO);
        String redisName = DetailTypeGetRedisName(detailType);
        List<String> keys = RedisUtil.keys(redisName);
        redisService.del(keys);
        redisService.zrem(REDIS_PAGE_ZSET,detailTypePO.getId());
        removeProductDetailCache(detailTypePO.getProductDetailId());
    }

    @Override
    public int update(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        int result = detailTypeDao.update(detailTypePO);
        redisService.hdel(REDIS_PAGE_HASH,detailType.getId());
        Integer detailTypeId = detailType.getId();
        Integer productDetailId = detailType.getProductDetailId();

        // 删除有关联的商品缓存
        removeProductCache(detailTypeId);
        // 删除有关联的可选项分类缓存
        removeProductDetailCache(productDetailId);

        return result;
    }

    @Override
    public PageInfo<DetailType> getByProductDetailId(DetailType detailType) {
        PageUtils.startPage(detailType);
        DetailTypePO detailTypePO = detailType.doForward();
        return getByProductDetailId(Lists.newArrayList(detailTypePO.getProductDetailId()));
    }

    public List<Product_DetailType_Middle> getByProductMiddle(int id) {
        return getByProductMiddle(Collections.singletonList(id));
    }

    /**
     * 通过DetailType和Product中间表查找映射关系
     * @param idList DetailTypeId
     * @return 返回DetailType和Product映射关系
     */
    @Override
    public List<Product_DetailType_Middle> getByProductMiddle(List<Integer> idList) {
        return detailTypeDao.getDetailTypeByProductId(idList);
    }

    @Override
    public List<DetailType> getByIds(List<Integer> idList) {
        List<DetailType> result = null;

        if(idList.size() > 0){
            result = RedisUtil.hmget(REDIS_PAGE_HASH
                    ,idList.stream().map(String::valueOf).collect(Collectors.toList())
                    ,DetailType.class);
        }else {
            return new ArrayList<>();
        }


        List<Integer> MissId = null;
        if (result != null) {
            if (result.size() != idList.size()) {
                MissId = new ArrayList<>();
                for (Integer integer : idList) {
                    boolean flag = false;
                    for (DetailType detailType : result) {
                        if (detailType.getId().equals(integer)) {
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
            List<DetailTypePO> poResult = detailTypeDao.getByIds(idList);
            List<DetailType> missResult = poResult.stream()
                    .map(DetailTypePO::convertToDetailType)
                    .collect(Collectors.toList());
            RedisUtil.hmset(REDIS_PAGE_HASH
                    ,missResult.stream().map(productDetail -> String.valueOf(productDetail.getId())).collect(Collectors.toList())
                    ,missResult);

            if(result == null){
                result = missResult;
            }else{
                result.addAll(missResult);
            }
        }
        return result;
    }

    @Override
    public void productAssociationDetailType(int productId, List<Integer> detailTypeIds) {
        // todo 后期需要改成批处理
        detailTypeIds.forEach(id -> {
            detailTypeDao.productAssociationDetailType(productId,id);
        });
    }

    @Override
    public void productDisconnectDetailType(int productId, List<Integer> detailTypeIds) {
        // todo 后期需要改成批处理
        detailTypeIds.forEach(id -> {
            detailTypeDao.productDisconnectDetailType(productId,id);
        });
    }

    @Override
    public List<Integer> getDetailTypeIdsByProductId(int productId) {
        List<Product_DetailType_Middle> middle = getByProductMiddle(productId);
        return middle.stream()
                .map(Product_DetailType_Middle::getDetailTypeId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getIdsByProductDetailId(Integer productDetailId) {
        return detailTypeDao.getIdsByProductDetailId(productDetailId);
    }

    /**
     * 删除可选项的Hash缓存
     */
    @Override
    public int removeDetailTypeCacheByDetailTypeId(Integer detailTypeId) {
        return removeDetailTypeCacheByDetailTypeId(Collections.singletonList(detailTypeId));
    }

    /**
     * 删除可选项的Hash缓存
     */
    @Override
    public int removeDetailTypeCacheByDetailTypeId(List<Integer> detailTypeId) {
        String result = redisService.hdel(REDIS_PAGE_HASH, detailTypeId);
        return Integer.parseInt(result);
    }

    @Override
    public Map<Integer, List<DetailType>> groupByProductDetailId(List<Integer> idList) {
        PageInfo<DetailType> result = getByProductDetailId(idList);
        List<DetailType> detailTypes = result.getList();
        return detailTypes.stream()
                .collect(Collectors.groupingBy(DetailType::getProductDetailId));
    }

    private int removeProductCache(Integer detailTypeId){
        List<Product_DetailType_Middle> middles =
                productService.getProductByDetailTypeId(Collections.singletonList(detailTypeId));
        List<Integer> productIds = middles.stream()
                        .map(Product_DetailType_Middle::getProductId)
                        .collect(Collectors.toList());
        if(productIds.size() > 0){
            return productService.removeProductCacheByProductId(productIds);
        }
        return 0;
    }

    private int removeProductDetailCache(Integer productDetailId){
        return productDetailService.removeProductDetailCache(productDetailId);
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

    private void removeAllCache(){
        String redisName = detailTypeAllGetRedisName();
        RedisUtil.del(redisName);
    }

    private String detailTypeAllGetRedisName(){
        return REDIS_PREFIX + ALL;
    }

    private List<String> DetailTypesIdsGetRedisName(List<Integer> ids) {
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

    private String DetailTypeGetRedisName(DetailType detailType) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (detailType != null) {
            result.append(REDIS_NAME).append(REDIS_MAP);
            if (detailType.getDetailTypeName() != null) {
                result.append(detailType.getDetailTypeName());
            } else {
                result.append("*");
            }
            result.append(REDIS_SEPARATE).append(REDIS_ID).append(REDIS_MAP);
            if (detailType.getId() != null) {
                result.append(detailType.getId());
            } else {
                result.append("*");
            }
        }
        return result.toString();
    }

    private String DetailTypeAllGetRedisName(DetailType detailType) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (detailType != null) {
            if (((Page) detailType).IsPage()) {
                result.append(REDIS_PAGE)
                        .append(REDIS_MAP)
                        .append(detailType.getPageNum());
                if (detailType.getOrderBy() != null) {
                    result.append(REDIS_SEPARATE)
                            .append(REDIS_ORDERBY)
                            .append(REDIS_MAP)
                            .append(detailType.getOrderBy());
                }
            } else {
                if (detailType.getId() == null) {
                    result.append("ALL");
                } else {
                    result.append(detailType.getId());
                }
            }
        }
        return result.toString();
    }

    private void getProductDetail(List<DetailType> detailTypes) {
        List<Integer> idList = detailTypes.stream()
                .map(DetailType::getProductDetailId)
                .collect(Collectors.toList());
        List<ProductDetail> productDetails = productDetailService.getByIds(idList);
        productDetails.forEach(productDetail -> {
            detailTypes.forEach(detailType -> {
                if (detailType.getProductDetailId().equals(productDetail.getId())) {
                    productDetail.setDetailTypeList(null);
                    detailType.setProductDetail(productDetail);
                }
            });
        });
    }

    private boolean validityProductDetailId(DetailType detailType) {
        ProductDetail productDetail = new ProductDetail();
        productDetail.setId(detailType.getProductDetailId());
        ProductDetail result = productDetailService.getById(productDetail);
        return result == null;
    }

    private PageInfo<DetailType> getByProductDetailId(List<Integer> idList) {
        List<DetailTypePO> result = detailTypeDao.getByProductDetailId(idList);
        List<DetailType> detailTypeList = result.stream()
                .map(DetailTypePO::convertToDetailType)
                .collect(Collectors.toList());
        return PageUtils.getPageInfo(result, detailTypeList);
    }

}
