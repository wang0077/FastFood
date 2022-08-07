package com.wang.fastfootstartredis.Util;

import com.github.pagehelper.PageInfo;
import com.wang.fastfootstartredis.enums.RedisOption;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.params.GeoRadiusParam;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @Auther: wAnG
 * @Date: 2021/11/28 17:04
 * @Description: !!! Spring赋值静态变量 ！！！
 * !!! 不要在Spring没有初始化容器之前使用这个类 !!!
 * !!! 除非你能知道Spring在什么时候初始化完这个类 !!!
 * !!! 不然就等着报空指针 !!!
 */

@Component
@Slf4j
public class RedisUtil {

    private static JedisPool jedisPool;

    private static RedissonClient redisson;

    private static final String LOCK = "-lock";


    @Autowired
    public void setJedisPool(JedisPool jedisPool) {
        RedisUtil.jedisPool = jedisPool;
    }

    @Autowired
    public void setRedisson(RedissonClient redisson) {
        RedisUtil.redisson = redisson;
    }

    private static Jedis getResource() {
        return jedisPool.getResource();
    }

    public static Pipeline getPipeline() {
        Jedis jedis = getResource();
        Pipeline pipelined = jedis.pipelined();
        pipelined.close();
        return jedis.pipelined();
    }

    private static String Serialization(Object value) {
        return JSONUtil.toJsonString(value);
    }

    private static <T> T Deserialization(String json, Class<T> clazz) {
        return JSONUtil.parse(json, clazz);
    }

    private static <T> List<T> DeserializationToList(String json, Class<T> clazz) {
        return JSONUtil.parseToList(json, clazz);
    }

    private static <T> PageInfo<T> DeserializationToPageInfo(String json, Class<T> clazz) {
        return JSONUtil.parseToPageInfo(json, clazz);
    }

    private static void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    private static void returnResource(Jedis jedis, Pipeline pipeline) {
        if (pipeline != null) {
            pipeline.close();
        }
        if (jedis != null) {
            jedis.close();
        }
    }

    public static RLock getLock(String lockName) {
        return redisson.getLock(lockName + LOCK);
    }

    public static List<GeoRadiusResponse> geoRadius(String key, GeoCoordinate geoCoordinate, double radius) {
        return geoRadius(key, geoCoordinate, radius, 5);
    }

    public static List<GeoRadiusResponse> geoRadius(String key, GeoCoordinate geoCoordinate, double radius, int count) {
        return geoRadius(key, geoCoordinate, radius, GeoUnit.KM, count);
    }

    public static List<GeoRadiusResponse> geoRadius(String key, GeoCoordinate geoCoordinate, double radius, int indexDB, int count) {
        return geoRadius(key, geoCoordinate, radius, GeoUnit.KM, indexDB, count);
    }

    public static List<GeoRadiusResponse> geoRadius(String key, GeoCoordinate geoCoordinate, double radius, GeoUnit unit, int count) {
        return geoRadius(key, geoCoordinate, radius, unit, 0, count);
    }

    public static List<GeoRadiusResponse> geoRadius(String key, GeoCoordinate geoCoordinate, double radius, GeoUnit unit, int indexDB, int count) {
        long totalTime;
        Jedis jedis = null;
        List<GeoRadiusResponse> result = null;
        try {
            jedis = getResource();
            jedis.select(indexDB);
            long startTime = System.currentTimeMillis();
            result = jedis.georadius(key, geoCoordinate.getLongitude()
                    , geoCoordinate.getLatitude(), radius, unit
                    , GeoRadiusParam.geoRadiusParam().withDist().sortAscending().count(count));
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogReadResultSuccess(totalTime, RedisOption.RADIUS, key, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.RADIUS, key, indexDB, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static void setGeo(String key, Map<String, GeoCoordinate> geoCoordinateMap) {
        setGeo(key, geoCoordinateMap, 0);
    }

    public static void setGeo(String key, Map<String, GeoCoordinate> geoCoordinateMap, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        Long result;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.geoadd(key, geoCoordinateMap);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.ADDGEO, key, geoCoordinateMap, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.ADDGEO, key, geoCoordinateMap, indexDB, e);
        } finally {
            returnResource(jedis);
        }
    }

    public static Long delGeo(String key, String memberNum) {
        return delGeo(key, memberNum, 0);
    }

    public static Long delGeo(String key, String memberNum, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.zrem(key, memberNum);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogDelResultSuccess(totalTime, RedisOption.DELGEO, result, indexDB, memberNum);
        } catch (Exception e) {
            RedisLog.LogDelResultError(RedisOption.DELGEO, e, indexDB, memberNum);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static void setGeo(String key, GeoCoordinate geoCoordinate, String memberNum) {
        setGeo(key, geoCoordinate, memberNum, 0);
    }

    public static void setGeo(String key, GeoCoordinate geoCoordinate, String memberNum, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        Long result;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.geoadd(key, geoCoordinate.getLongitude(), geoCoordinate.getLatitude(), memberNum);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.ADDGEO, memberNum, geoCoordinate, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.ADDGEO, memberNum, geoCoordinate, indexDB, e);
        } finally {
            returnResource(jedis);
        }
    }

    public static List<String> keys(List<String> keys) {
        return keys(keys, 0);
    }

    public static List<String> keys(List<String> keys, int indexDB) {
        long totalTime;
        List<String> result = new ArrayList<>();
        Jedis jedis = null;
        Pipeline pipeline = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            pipeline = jedis.pipelined();
            keys.forEach(pipeline::keys);
            pipeline.sync();
            result = (List<String>) (List) pipeline.syncAndReturnAll();
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogReadResultSuccess(totalTime, RedisOption.PIPEKEYS, keys.toString(), indexDB, result);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.KEYS, keys.toString(), indexDB, e);
        } finally {
            returnResource(jedis, pipeline);
        }
        return result;
    }

    public static List<String> keys(String key) {
        return keys(key, 0);
    }

    public static List<String> keys(String key, int indexDB) {
        long totalTime;
        Set<String> set;
        List<String> result = null;
        Jedis jedis = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = jedisPool.getResource();
            jedis.select(indexDB);
            set = jedis.keys(key);
            result = new ArrayList<>(set);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogReadResultSuccess(totalTime, RedisOption.KEYS, key, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.KEYS, key, indexDB, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static <T> String mset(List<String> key, List<T> value) {
        return mset(key, value, 0);
    }

    public static <T> String mset(List<String> key, List<T> value, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        Pipeline pipelined = null;
        String result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            pipelined = jedis.pipelined();
            jedis.select(indexDB);
            for (int i = 0; i < key.size(); i++) {
                pipelined.set(key.get(i), Serialization(value.get(i)));
            }
            pipelined.sync();
            result = (String) pipelined.syncAndReturnAll().get(0);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.MSET, key.toString(), value.toString(), indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.MSET, key.toString(), value.toString(), indexDB, e);
        } finally {
            returnResource(jedis, pipelined);
        }
        return result;
    }

    public static <T> List<T> mget(Class<T> clazz, List<String> keys) {
        if (keys == null) {
            return null;
        }
        String[] strings = keys.toArray(new String[0]);
        return mget(clazz, strings);
    }

    public static <T> List<T> mget(Class<T> clazz, String... keys) {
        return mget(0, clazz, keys);
    }

    public static <T> List<T> mget(int indexDB, Class<T> clazz, String... keys) {
        long totalTime;
        List<String> jsonList;
        List<T> result = null;
        Jedis jedis = null;
        if (keys.length == 0) {
            return null;
        }
        try {
            long startTime = System.currentTimeMillis();
            jedis = jedisPool.getResource();
            jedis.select(indexDB);
            jsonList = jedis.mget(keys);
            result = jsonList.stream()
                    .map(json -> JSONUtil.parse(json, clazz))
                    .collect(Collectors.toList());
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogReadResultSuccess(totalTime, RedisOption.GET, Arrays.toString(keys), indexDB, result);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.GET, Arrays.toString(keys), indexDB, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public String setex(String key, Object value, int seconds) {
        return setex(key, value, seconds, 0);
    }

    public String setex(String key, Object value, int seconds, int indexDB) {
        long totalTime;
        String valueJson = Serialization(value);
        Jedis jedis = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            String result = jedis.setex(key, seconds, valueJson);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.SETEX, key, value.toString() + "| Seconds : [" + seconds + "] |", indexDB, result);
//            log.info("[Redis ({})] ==> option : [{}] | key : [{}] | setTime : [{}] | value : [{}] | indexDB : [{}] \n ", result, RedisOption.GET, key, seconds, value, indexDB);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.SETEX, key, value.toString() + "| Seconds : [" + seconds + "] |", indexDB, e);
//            log.error("[Redis] ==> option : [{}] | Key : [{}] | setTime : [{}] | value : [{}] | indexDB : [{}] \n | errorMsg : [{}]", RedisOption.SET, key, seconds, value, indexDB, e.getMessage());
//            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public static <T> PageInfo<T> getByPageInfo(String key, Class<T> clazz) {
        return getByPageInfo(key, clazz, 0);
    }

    public static <T> PageInfo<T> getByPageInfo(String key, Class<T> clazz, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        String valueJson;
        PageInfo<T> value = null;
        if (key == null) {
            return null;
        }
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            valueJson = jedis.get(key);
            if (valueJson == null) {
                return null;
            }
            value = DeserializationToPageInfo(valueJson, clazz);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogReadResultSuccess(totalTime, RedisOption.GET, key, indexDB, value);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.GET, key, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public static <T> List<T> getByList(String key, Class<T> clazz) {
        return getByList(key, clazz, 0);
    }

    public static <T> List<T> getByList(String key, Class<T> clazz, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        String valueJson;
        List<T> value = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            valueJson = jedis.get(key);
            if (valueJson == null) {
                return null;
            }
            value = DeserializationToList(valueJson, clazz);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogReadResultSuccess(totalTime, RedisOption.GET, key, indexDB, value);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.GET, key, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public static <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, 0);
    }

    public static <T> T get(String key, Class<T> clazz, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        String valueJson;
        T value = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            valueJson = jedis.get(key);
            if (valueJson == null) {
                return null;
            }
            value = Deserialization(valueJson, clazz);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogReadResultSuccess(totalTime, RedisOption.GET, key, indexDB, value);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.GET, key, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public static <T> List<T> hmget(String key,List<String> field,Class<T> clazz){
        return hmget(key,field,clazz,0);
    }

    public static <T> List<T> hmget(String key,List<String> field,Class<T> clazz,int indexDB){
        long totalTime;
        Jedis jedis = null;
        List<T> result = null;
        String[] fields = new String[field.size()];
        field.toArray(fields);
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            List<String> jsons = jedis.hmget(key, fields);
            result = jsons.stream()
                    .filter(Objects::nonNull)
                    .map(json -> JSONUtil.parse(json,clazz))
                    .collect(Collectors.toList());
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogReadResultSuccess(totalTime,RedisOption.HMGET,key,indexDB,result);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.HMGET,key,indexDB,e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static <T> T hget(String key,String field,Class<T> clazz){
        return hget(key,field,clazz,0);
    }

    public static <T> T hget(String key,String field,Class<T> clazz,int indexDB){
        long totalTime;
        Jedis jedis = null;
        T result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            String json = jedis.hget(key, field);
            result = JSONUtil.parse(json,clazz);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogReadResultSuccess(totalTime,RedisOption.HSET,key,indexDB,result);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.HSET,key,indexDB,e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long hdel(String key,Integer field){
        return hdel(key,Collections.singletonList(field));
    }

    public static Long hdel(String key,List<Integer> fields){
        return hdel(key,fields,0);
    }

    public static Long hdel(String key,List<Integer> fields,int indexDB){
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        List<String> tempKey = fields.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        String[] keys = new String[tempKey.size()];
        tempKey.toArray(keys);
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.hdel(key, keys);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogDelResultSuccess(totalTime,RedisOption.HDEL,result,indexDB,keys);
        } catch (Exception e) {
            RedisLog.LogDelResultError(RedisOption.HDEL,e,indexDB,keys);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }


    public static <T> String hmset(String key,List<String> field,List<T> value){
        return hmset(key,field,value,0);
    }

    public static <T> String hmset(String key,List<String> field,List<T> value,int indexDB){
        long totalTime;
        Jedis jedis = null;

        // 先对Object进行序列化
        List<String> valueJson = value.stream()
                .map(RedisUtil::Serialization)
                .collect(Collectors.toList());

        // 将field和valueJson进行合并成Map结构（Jedis需要这样的结构）
        Map<String, String> json = field.stream()
                .collect(Collectors.toMap(mapKey -> mapKey
                        , mapKey -> valueJson.get(field.indexOf(mapKey))));

        String result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.hmset(key, json);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime,RedisOption.HMSET,key,value,indexDB,result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.HMSET,key,value,indexDB,e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }


    public static Long hset(String key,String field,Object value){
        return hset(key,field,value,0);
    }

    public static Long hset(String key,String field,Object value,int indexDB){
        long totalTime;
        Jedis jedis = null;
        String resultJson = Serialization(value);
        Long result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.hset(key, field, resultJson);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime,RedisOption.HSET,key,value,indexDB,result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.HSET,key,value,indexDB,e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     *  ZSet按照索引区间进行查找，查找的区间是 start <= index <= end 范围的数据
     */
    public static List<Integer> zrange(String key,int start,int end){
        return zrange(key,start,end,0);
    }

    public static List<Integer> zrange(String key, int start, int end, int indexDB){
        long totalTime;
        Jedis jedis = null;
        List<Integer> result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            Set<String> zrange = jedis.zrange(key, start, end);
            result = new ArrayList<>(zrange)
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogReadResultSuccess(totalTime,RedisOption.ZRANGE,key,indexDB,result);
        } catch (Exception e) {
            RedisLog.LogDelResultError(RedisOption.ZREM,e,indexDB,key);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long zrem(String key,Integer member){
        return zrem(key,Collections.singletonList(member));
    }

    public static Long zrem(String key,List<Integer> member){
        return zrem(key,member,0);
    }

    public static Long zrem(String key,List<Integer> member,int indexDB){
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        String[] members = new String[member.size()];
        member.stream()
                .map(String::valueOf)
                .collect(Collectors.toList())
                .toArray(members);

        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.zrem(key,members);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogDelResultSuccess(totalTime, RedisOption.ZREM,result,indexDB,key);
        } catch (Exception e) {
            RedisLog.LogDelResultError(RedisOption.ZREM,e,indexDB,key);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long zremrangeByScore(String key,Long member){
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(0);
            result = jedis.zremrangeByScore(key,member,member);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogDelResultSuccess(totalTime, RedisOption.ZREMRANGEBYSCORE,result,0,key);
        } catch (Exception e) {
            RedisLog.LogDelResultError(RedisOption.ZREM,e,0,key);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long zrevrank(String key,Long member){
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(0);
            result = jedis.zrevrank(key,member.toString());
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogDelResultSuccess(totalTime, RedisOption.ZREVRANK,result,0,key);
        } catch (Exception e) {
            RedisLog.LogDelResultError(RedisOption.ZREM,e,0,key);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long zcard(String key){
        return zcard(key,0);
    }

    public static Long zcard(String key,int indexDB){
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.zcard(key);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.ZCARD, key, null, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.ZCARD, key, null, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long zadd(String key,long score,Object value){
        return zadd(key,score,value,0);
    }


    public static Long zadd(String key,long score,Object value,int indexDB){
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        String resultJson = Serialization(value);
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.zadd(key,score,resultJson);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.ZADD, key, value, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.ZADD, key, value, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static String set(String key, Object value) {
        return set(key, value, 0);
    }

    public static String set(String key, Object value, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        String result = null;
        String resultJson = Serialization(value);
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.set(key, resultJson);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.GET, key, value, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.SET, key, value, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long del(List<String> keys) {
        return del(keys.toArray(new String[0]));
    }

    public static Long del(String... keys) {
        return del(0, keys);
    }

    public static Long del(int indexDB, String... keys) {
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.del(keys);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogDelResultSuccess(totalTime, RedisOption.DEL, result, indexDB, keys);
        } catch (Exception e) {
            RedisLog.LogDelResultError(RedisOption.DEL, e, indexDB, keys);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.exists(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            returnResource(jedis);
        }
    }

    public static String flushDB() {
        Jedis jedis = null;
        try {
            jedis = getResource();
            return jedis.flushDB();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public static Long expire(String key, int value, int indexDB) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(indexDB);
            return jedis.expire(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            returnResource(jedis);
        }
    }

    public static Long ttl(String key, int indexDB) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.select(indexDB);
            return jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            returnResource(jedis);
        }
    }

    public static Long incr(String key) {
        return incr(key, 0);
    }

    public static Long incr(String key, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.incr(key);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.INCR, key, 1, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.INCR, key, 1, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long incrBy(String key, Long value) {
        return incrBy(key, value, 0);
    }

    public static Long incrBy(String key, Long value, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            result = jedis.incrBy(key, value);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.INCRBY, key, value, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.INCRBY, key, value, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static long decr(String key) {
        return decr(key, 0);
    }


    public static Long decr(String key, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.decr(key);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.DECR, key, -1, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.DECR, key, -1, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long decrBy(String key, Long value) {
        return decrBy(key, value, 0);
    }


    public static Long decrBy(String key, Long value, int indexDB) {
        long totalTime;
        Jedis jedis = null;
        Long result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = jedisPool.getResource();
            jedis.select(indexDB);
            result = jedis.decrBy(key, value);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            RedisLog.LogWriteResultSuccess(totalTime, RedisOption.INCRBY, key, value, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.DECR, key, -1, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }


    public static class RedisLog {
        private static void LogReadResultSuccess(Long totalTime, RedisOption op, String key, int indexDB, Object result) {
            log.info("[Redis (totalTime : {}ms)] ==> option : [{}] | key : [{}] | indexDB : [{}] \n | Result : [{}]", totalTime, op.getOpName(), key, indexDB, result);
        }

        private static void LogReadResultError(RedisOption op, String key, int indexDB, Exception e) {
            log.error("[Redis] ==> option : [{}] | Key : [{}] | indexDB : [{}] \n | errorMsg : [{}]", op.getOpName(), key, indexDB, e.getMessage());
        }

        private static void LogWriteResultSuccess(Long totalTime, RedisOption op, String key, Object value, int indexDB, Object result) {
            if (("OK").equals(result) || (!(result instanceof String) && result != null)) {
                result = "Success";
            } else {
                result = "Fail";
            }
            log.info("[Redis ({}) == (TotalTime : {}ms)] ==> option : [{}] | key : [{}] | value : [{}] | indexDB : [{}] \n ", result, totalTime, op.getOpName(), key, value, indexDB);
        }

        private static void LogWriteResultError(RedisOption op, String key, Object value, int indexDB, Exception e) {
            log.error("[Redis] ==> option : [{}] | Key : [{}] | value : [{}] | indexDB : [{}] \n | errorMsg : [{}]", op.getOpName(), key, value, indexDB, e.getMessage());
        }

        private static void LogDelResultSuccess(Long totalTime, RedisOption op, Long result, int indexDB, String... keys) {
            log.info("[Redis (OptionCount : {}) == (TotalTime : {}ms)] ==> option : [{}] | key : [{}] |  indexDB : [{}]", result, totalTime, op.getOpName(), keys, indexDB);
        }

        private static void LogDelResultError(RedisOption op, Exception e, int indexDB, String... keys) {
            log.error("[Redis] ==> option : [{}] | Key : [{}] | indexDB : [{}] \n| errorMsg : [{}]", op.getOpName(), Arrays.asList(keys), indexDB, e.getMessage());
        }
    }
}
