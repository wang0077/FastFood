package com.wang.storeCenter.service.impl;

import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Util.PageUtils;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.Response;
import com.wang.fastfood.apicommons.entity.request.addAdminRequest;
import com.wang.fastfood.apicommons.enums.SqlResultEnum;
import com.wang.fastfootstartredis.Redis.AsyncRedis;
import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.storeCenter.dao.StoreDao;
import com.wang.storeCenter.entity.BO.Store;
import com.wang.storeCenter.entity.BO.StoreRadius;
import com.wang.storeCenter.entity.PO.StorePO;
import com.wang.storeCenter.fegin.UserClient;
import com.wang.storeCenter.service.IStoreService;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/1/8 17:07
 * @Description:
 */

@Service
public class storeService implements IStoreService {

    @Autowired
    private StoreDao storeDao;

    @Autowired
    private AsyncRedis asyncRedis;

    @Autowired
    private UserClient userClient;

    private static final String STORE_RADIUS = "storeRadius";

    private static final String ALL = "ALL";

    private static final String REDIS_PREFIX = "Store-";

    private static final String REDIS_PAGE = "page";

    private static final String REDIS_NAME = "name";

    private static final String REDIS_MAP = ":";

    private static final String REDIS_ID = "id";

    private static final String REDIS_ORDERBY = "orderBy";

    private static final String REDIS_SEPARATE = "-";


    @Override
    public PageInfo<Store> getAll(Store store) {
        PageInfo<Store> result;
        String redisName = storeAllGetRedisName(store);
        result = RedisUtil.getByPageInfo(redisName, Store.class);
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    PageUtils.startPage(store);
                    List<StorePO> poResult = storeDao.getStoreAll();
                    List<Store> storeList = poResult.stream()
                            .map(StorePO::convertToStore)
                            .collect(Collectors.toList());
                    result = PageUtils.getPageInfo(poResult, storeList);
                    asyncRedis.set(redisName, result);
                } finally {
                    lock.unlock();
                }
            }
        }

        return result;
    }

    @Override
    public int insert(Store store) {
        asyncRemovePageCache();
        if (store.getId() == null && validityStoreExist(store)) {
            return SqlResultEnum.REPEAT_INSERT.getValue();
        }
        StorePO storePO = store.doForward();
        int result = storeDao.add(storePO);
        Store compStore = getByName(store);
        Long id = compStore.getId();
        addAdminRequest request = buildAddAdminRequest(store.getStorePhoneNumber(),id);
        Response<Integer> response = userClient.addAdmin(request);
        String redisName = storeGetRedisName(compStore);
        RedisUtil.setGeo(STORE_RADIUS,buildGeoCoordinate(store),redisName);
        syncRemovePageCache();
        removeAllCache();
        return result;
    }

    @Override
    public Store getByName(Store store) {
        Store result = null;
        String redisName = storeGetRedisName(store);
        List<String> keys = RedisUtil.keys(redisName);
        List<Store> storeList = RedisUtil.mget(Store.class, keys);
        if (storeList != null && storeList.size() > 0) {
            result = storeList.get(0);
        }
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    StorePO storePO = store.doForward();
                    StorePO poResult = storeDao.getByName(storePO);
                    result = poResult == null ? null : poResult.convertToStore();
                    if (result != null) {
                        redisName = storeGetRedisName(result);
                        asyncRedis.set(redisName, result);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        return result;
    }

    @Override
    public Store getById(Store store) {
        Store result = null;
        String redisName = storeGetRedisName(store);
        List<String> keys = RedisUtil.keys(redisName);
        List<Store> storeList = RedisUtil.mget(Store.class, keys);
        if (storeList != null &&storeList.size() > 0) {
            result = storeList.get(0);
        }
        if (result == null) {
            RLock lock = RedisUtil.getLock(redisName);
            if (lock.tryLock()) {
                try {
                    StorePO storePO = store.doForward();
                    StorePO poResult = storeDao.getById(storePO);
                    result = poResult == null ? null : poResult.convertToStore();
                    if (result != null) {
                        redisName = storeGetRedisName(result);
                        asyncRedis.set(redisName, result);
                    }
                } finally {
                    lock.unlock();
                }
            }

        }
        return result;
    }

    @Override
    public List<Store> getByIds(List<Integer> idList) {
        return storeDao.getByIds(idList).stream()
                .map(StorePO::convertToStore)
                .collect(Collectors.toList());
    }

    @Override
    public PageInfo<Store> getLikeName(Store store) {
        PageInfo<Store> result;
        PageUtils.startPage(store);
        StorePO storePO = store.doForward();
        List<StorePO> poResult = storeDao.getLikeName(storePO);
        List<Store> storeList = poResult.stream()
                .map(StorePO::convertToStore)
                .collect(Collectors.toList());
        result = PageUtils.getPageInfo(poResult, storeList);
        return result;
    }

    @Override
    public int update(Store store) {
        syncRemovePageCache();
        StorePO storePO = store.doForward();
        int result = storeDao.update(storePO);
        asyncRemovePageCache();
        // todo 如果前端只返回修改部分的数据可以考虑判断经纬度和名字是否改变
        //      如果没有改变则不要去改变地图数据
        return result;
    }

    @Override
    public int remove(Store store) {
        String redisName = storeGetRedisName(store);
        syncRemovePageCache();
        StorePO storePO = store.doForward();
        int result = storeDao.remove(storePO);
        asyncRemovePageCache();
        List<String> keys = RedisUtil.keys(redisName);
        if(keys.size() > 0){
            redisName = keys.get(0);
        }
        // 删除门店缓存数据
        asyncRedis.del(redisName);
        // 删除门店地图数据
        asyncRedis.delGeo(STORE_RADIUS,redisName);
        removeAllCache();
        return result;
    }

    @Override
    public List<StoreRadius> storeRadius(GeoCoordinate geoCoordinate, double radius) {
        List<GeoRadiusResponse> geoRadiusResponses = RedisUtil
                .geoRadius(STORE_RADIUS, geoCoordinate, radius);
        List<StoreRadius> storeRadiusList = buildStoreRadius(geoRadiusResponses);
        List<String> keys = buildRedisNameByStoreRadius(storeRadiusList);
        List<Store> storeList = RedisUtil.mget(Store.class, keys);
        storeRadiusList.forEach(storeRadius -> {
            storeList.stream()
                    .filter(store -> storeRadius.getMember().equals(storeGetRedisName(store)))
                    .findFirst()
                    .ifPresent(storeRadius::setStore);
        });
        checkStoreIsBusiness(storeRadiusList);
        return storeRadiusList;
    }

    @Override
    public StoreRadius nearbyStore(GeoCoordinate geoCoordinate, double radius) {
        List<StoreRadius> stores = storeRadius(geoCoordinate, radius);
        return stores.stream().min(Comparator.comparing(StoreRadius::getDistance)).orElse(null);
    }

    private addAdminRequest buildAddAdminRequest(String userName,Long StoreId){
        addAdminRequest request = new addAdminRequest();
        request.setUserName(userName);
        request.setStoreId(String.valueOf(StoreId));
        return request;
    }

    private String getStoreUserName(Long storeId){
        return "Store" + storeId;
    }

    private void checkStoreIsBusiness(List<StoreRadius> storeRadiusList){
        storeRadiusList.forEach(store -> {
            LocalTime localTime = LocalTime.now();
            LocalTime startTime = LocalTime.parse(store.getStore().getStartTime());
            LocalTime endTime = LocalTime.parse(store.getStore().getEndTime());
            store.setBusiness(localTime.isAfter(startTime) & localTime.isBefore(endTime));
        });
    }

    private List<String> buildRedisNameByStoreRadius(List<StoreRadius> storeRadiusList){
        return storeRadiusList.stream().map(StoreRadius::getMember).collect(Collectors.toList());
    }

    private List<StoreRadius> buildStoreRadius(List<GeoRadiusResponse> geoRadiusList){
        return geoRadiusList.stream().map(geoRadiusResponse -> {
            StoreRadius storeRadius = new StoreRadius();
            storeRadius.setMember(new String(geoRadiusResponse.getMember(), StandardCharsets.UTF_8));
            storeRadius.setDistance(geoRadiusResponse.getDistance());
            return storeRadius;
        }).collect(Collectors.toList());
    }

    private GeoCoordinate buildGeoCoordinate(Store store){
        return new GeoCoordinate(store.getStoreLongitude(),store.getStoreLatitude());
    }

    private Map<String, GeoCoordinate> buildGeoCoordinate(List<Store> storeList) {
        return storeList.stream().collect(Collectors.toMap(
                store -> String.valueOf(store.getId())
                , store -> new GeoCoordinate(store.getStoreLongitude(), store.getStoreLatitude())));
    }

    private boolean validityStoreExist(Store store) {
        Store result = getByName(store);
        return result != null;
    }

    /**
     * 清除**-ALL的缓存
     */
    private void removeAllCache(){
        String redisName = storeAllGetRedisName();
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
        asyncRedis.del(keys);
    }

    private String storeAllGetRedisName(){
        return REDIS_PREFIX + ALL;
    }

    private String storeGetRedisName(Store store) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (store != null) {
            result.append(REDIS_NAME).append(REDIS_MAP);
            if (store.getStoreName() != null) {
                result.append(store.getStoreName());
            } else {
                result.append("*");
            }
            result.append(REDIS_SEPARATE).append(REDIS_ID).append(REDIS_MAP);
            if (store.getId() != null) {
                result.append(store.getId());
            } else {
                result.append("*");
            }
        }
        return result.toString();
    }

    private String storeAllGetRedisName(Store store) {
        StringBuilder result = new StringBuilder();
        result.append(REDIS_PREFIX);
        if (store != null) {
            if (((Page) store).IsPage()) {
                result.append(REDIS_PAGE)
                        .append(REDIS_MAP)
                        .append(store.getPageNum());
                if (store.getOrderBy() != null) {
                    result.append(REDIS_SEPARATE)
                            .append(REDIS_ORDERBY)
                            .append(REDIS_MAP)
                            .append(store.getOrderBy());
                }
            } else {
                if (store.getId() == null) {
                    result.append("ALL");
                } else {
                    result.append(store.getId());
                }
            }
        }
        return result.toString();
    }
}
