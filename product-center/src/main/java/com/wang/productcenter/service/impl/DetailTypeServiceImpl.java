package com.wang.productcenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.productcenter.Redis.RedisService;
import com.wang.productcenter.Util.RedisUtil;
import com.wang.productcenter.dao.DetailTypeDao;
import com.wang.productcenter.entity.BO.DetailType;
import com.wang.productcenter.entity.BO.ProductDetail;
import com.wang.productcenter.entity.PO.DetailTypePO;
import com.wang.productcenter.entity.PO.Product_DetailType_Middle;
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
    private RedisService redisService;

    private static final String REDIS_PREFIX = "Detail-Type-";

    private static final String ALL = "ALL";

    private static final String REDIS_PAGE = "page";

    private static final String REDIS_NAME = "name";

    private static final String REDIS_MAP = ":";

    private static final String REDIS_ID = "id";

    private static final String REDIS_ORDERBY = "orderBy";

    private static final String REDIS_SEPARATE = "-";

    public int insert(DetailType detailType) {
        DetailTypePO detailTypePO = detailType.doForward();
        // 商品详情ID未传入或商品详情ID在对应表不存在返回插入失败
        if (detailTypePO.getProductDetailId() == null || validityProductDetailId(detailType)) {
            return SqlResultEnum.ERROR_INSERT.getValue();
        }
        DetailTypePO result = detailTypeDao.getByName(detailTypePO);
        if (result != null) {
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        asyncRemovePageCache();
        return detailTypeDao.insert(detailTypePO);
    }

    @Override
    public PageInfo<DetailType> getAll(DetailType detailType) {
        PageInfo<DetailType> result = null;
        String redisName = DetailTypeAllGetRedisName(detailType);
        result = RedisUtil.getByPageInfo(redisName, DetailType.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    PageUtils.startPage(detailType);
                    List<DetailTypePO> poResult = detailTypeDao.getAll();
                    List<DetailType> detailTypeList = poResult.stream()
                            .map(DetailTypePO::convertToDetailType)
                            .collect(Collectors.toList());
                    getProductDetail(detailTypeList);
                    result = PageUtils.getPageInfo(poResult, detailTypeList);
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

    @Override
    public DetailType getById(DetailType detailType) {
        DetailType result = null;
        String redisName = DetailTypeGetRedisName(detailType);
        result = RedisUtil.get(redisName, DetailType.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    DetailTypePO detailTypePO = detailType.doForward();
                    DetailTypePO poResult = detailTypeDao.getById(detailTypePO);
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
        detailTypeDao.remove(detailTypePO);
        String redisName = DetailTypeGetRedisName(detailType);
        List<String> keys = RedisUtil.keys(redisName);
        if (keys == null || keys.size() == 0) {
            return;
        }
        redisService.del(keys);
    }

    @Override
    public int update(DetailType detailType) {
        syncRemovePageCache();
        DetailTypePO detailTypePO = detailType.doForward();
        int result = detailTypeDao.update(detailTypePO);
        asyncRemovePageCache();
        removeAllCache();
        return result;
    }

    @Override
    public PageInfo<DetailType> getByProductDetailId(DetailType detailType) {
        PageUtils.startPage(detailType);
        DetailTypePO detailTypePO = detailType.doForward();
        return getByProductDetailId(Lists.newArrayList(detailTypePO.getProductDetailId()));
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
        List<String> redisNameList = DetailTypesIdsGetRedisName(idList);
        List<String> keys = RedisUtil.keys(redisNameList);
        if (keys != null && keys.size() > 0) {
            result = RedisUtil.mget(DetailType.class, keys);
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
            result = poResult.stream()
                    .map(DetailTypePO::convertToDetailType)
                    .collect(Collectors.toList());
            redisNameList = new ArrayList<>();
            for (DetailType detailType : result) {
                redisNameList.add(DetailTypeGetRedisName(detailType));
            }
            redisService.mset(redisNameList,result);
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
        List<Product_DetailType_Middle> middle = getByProductMiddle(new ArrayList<>(productId));
        return middle.stream()
                .map(Product_DetailType_Middle::getDetailTypeId)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, List<DetailType>> groupByProductDetailId(List<Integer> idList) {
        PageInfo<DetailType> result = getByProductDetailId(idList);
        List<DetailType> detailTypes = result.getList();
        return detailTypes.stream()
                .collect(Collectors.groupingBy(DetailType::getProductDetailId));
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
