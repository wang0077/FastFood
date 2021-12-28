package com.wang.productcenter.Util;

import com.github.pagehelper.PageInfo;
import com.wang.productcenter.enums.RedisOption;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

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

    public static Pipeline getPipeline(){
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
        return JSONUtil.parseToList(json,clazz);
    }

    private static <T> PageInfo<T> DeserializationToPageInfo(String json, Class<T> clazz) {
        return JSONUtil.parseToPageInfo(json,clazz);
    }

    private static void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    private static void returnResource(Jedis jedis,Pipeline pipeline){
        if(pipeline != null){
            pipeline.close();
        }
        if(jedis != null){
            jedis.close();
        }
    }

    public static RLock getLock(String lockName){
        return redisson.getLock(lockName + LOCK);
    }

    public static List<String> keys(List<String> keys){
        return keys(keys,0);
    }

    public static List<String> keys(List<String> keys,int indexDB){
        List<String> result = new ArrayList<>();
        Jedis jedis = null;
        Pipeline pipeline = null;
        try {
            jedis = getResource();
            jedis.select(indexDB);
            pipeline = jedis.pipelined();
            keys.forEach(pipeline::keys);
            pipeline.sync();
            result = (List<String>)(List) pipeline.syncAndReturnAll();
            RedisLog.LogReadResultSuccess(RedisOption.PIPEKEYS,keys.toString(),indexDB,result);
        }catch (Exception e){
            RedisLog.LogReadResultError(RedisOption.KEYS, keys.toString(), indexDB, e);
        }finally {
            returnResource(jedis,pipeline);
        }
        return result;
    }

    public static List<String> keys(String key){
        return keys(key,0);
    }

    public static List<String> keys(String key,int indexDB){
        Set<String> set = null;
        List<String> result = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(indexDB);
            set = jedis.keys(key);
            result = new ArrayList<>(set);
            RedisLog.LogReadResultSuccess(RedisOption.KEYS, key, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.KEYS, key, indexDB, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static<T> String mset(List<String> key,List<T> value){
        return mset(key,value,0);
    }

    public static<T> String mset(List<String> key,List<T> value,int indexDB){
        Jedis jedis = null;
        Pipeline pipelined = null;
        String result = null;
        try {
            jedis = getResource();
            pipelined = jedis.pipelined();
            jedis.select(indexDB);
            for(int i = 0;i < key.size();i++){
                pipelined.set(key.get(i),Serialization(value.get(i)));
            }
            pipelined.sync();
            result = (String) pipelined.syncAndReturnAll().get(0);
            RedisLog.LogWriteResultSuccess(RedisOption.MSET,key.toString(),value.toString(),indexDB,result);
        }catch (Exception e){
            RedisLog.LogWriteResultError(RedisOption.MSET,key.toString(),value.toString(),indexDB,e);
        }finally {
            returnResource(jedis,pipelined);
        }
        return result;
    }

    public static <T> List<T> mget(Class<T> clazz,List<String> keys){
        if(keys == null){
            return null;
        }
        String[] strings = keys.toArray(new String[0]);
        return mget(clazz,strings);
    }

    public static <T> List<T> mget(Class<T> clazz, String... keys) {
        return mget(0, clazz, keys);
    }

    public static <T> List<T> mget(int indexDB, Class<T> clazz, String... keys) {
        List<String> jsonList = null;
        List<T> result = null;
        Jedis jedis = null;
        if(keys.length == 0){
            return null;
        }
        try {
            jedis = jedisPool.getResource();
            jedis.select(indexDB);
            jsonList = jedis.mget(keys);
            result = jsonList.stream()
                    .map(json -> JSONUtil.parse(json,clazz))
                    .collect(Collectors.toList());
            RedisLog.LogReadResultSuccess(RedisOption.GET, Arrays.toString(keys), indexDB, result);
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
        String valueJson = Serialization(value);
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.select(indexDB);
            String result = jedis.setex(key, seconds, valueJson);
            log.info("[Redis ({})] ==> option : [{}] | key : [{}] | setTime : [{}] | value : [{}] | indexDB : [{}] \n ", result, RedisOption.GET, key, seconds, value, indexDB);
        } catch (Exception e) {
            log.error("[Redis] ==> option : [{}] | Key : [{}] | setTime : [{}] | value : [{}] | indexDB : [{}] \n | errorMsg : [{}]", RedisOption.SET, key, seconds, value, indexDB, e.getMessage());
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public static <T> PageInfo<T> getByPageInfo(String key,Class<T> clazz){
        return getByPageInfo(key,clazz,0);
    }

    public static <T> PageInfo<T> getByPageInfo(String key, Class<T> clazz, int indexDB){
        long startTime = System.currentTimeMillis();
        Jedis jedis = null;
        String valueJson = null;
        PageInfo<T> value = null;
        if(key == null){
            return null;
        }
        try {
            jedis = getResource();
            jedis.select(indexDB);
            valueJson = jedis.get(key);
            if(valueJson == null){
                return null;
            }
            value = DeserializationToPageInfo(valueJson, clazz);
            RedisLog.LogReadResultSuccess(RedisOption.GET, key, indexDB, value);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("totalTime : {}ms",endTime - startTime);
            log.error("线程等待数 : " + jedisPool.getNumWaiters());
            RedisLog.LogReadResultError(RedisOption.GET, key, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public static <T> List<T> getByList(String key,Class<T> clazz){
        return getByList(key,clazz,0);
    }

    public static <T> List<T> getByList(String key,Class<T> clazz,int indexDB){
        Jedis jedis = null;
        String valueJson = null;
        List<T> value = null;
        try {
            jedis = getResource();
            jedis.select(indexDB);
            valueJson = jedis.get(key);
            if(valueJson == null){
                return null;
            }
            value = DeserializationToList(valueJson, clazz);
            RedisLog.LogReadResultSuccess(RedisOption.GET, key, indexDB, value);
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
        Jedis jedis = null;
        String valueJson = null;
        T value = null;
        try {
            jedis = getResource();
            jedis.select(indexDB);
            valueJson = jedis.get(key);
            if(valueJson == null){
                return null;
            }
            value = Deserialization(valueJson, clazz);
            RedisLog.LogReadResultSuccess(RedisOption.GET, key, indexDB, value);
        } catch (Exception e) {
            RedisLog.LogReadResultError(RedisOption.GET, key, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public static String set(String key, Object value) {
        return set(key, value, 0);
    }

    public static String set(String key, Object value, int indexDB) {
        Jedis jedis = null;
        String result = null;
        String resultJson = Serialization(value);
        try {
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.set(key, resultJson);
            RedisLog.LogWriteResultSuccess(RedisOption.GET, key, value, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.SET, key, value, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long del(List<String> keys){
        return del(keys.toArray(new String[0]));
    }

    public static Long del(String... keys) {
        return del(0, keys);
    }

    public static Long del(int indexDB, String... keys) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.del(keys);
            RedisLog.LogDelResultSuccess(RedisOption.DEL, result, indexDB, keys);
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
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.incr(key);
            RedisLog.LogWriteResultSuccess(RedisOption.INCR, key, 1, indexDB, result);
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
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = getResource();
            result = jedis.incrBy(key, value);
            RedisLog.LogWriteResultSuccess(RedisOption.INCRBY, key, value, indexDB, result);
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
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = getResource();
            jedis.select(indexDB);
            result = jedis.decr(key);
            RedisLog.LogWriteResultSuccess(RedisOption.DECR, key, -1, indexDB, result);
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
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(indexDB);
            result = jedis.decrBy(key, value);
            RedisLog.LogWriteResultSuccess(RedisOption.INCRBY, key, value, indexDB, result);
        } catch (Exception e) {
            RedisLog.LogWriteResultError(RedisOption.DECR, key, -1, indexDB, e);
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
    }


    public static class RedisLog {
        private static void LogReadResultSuccess(RedisOption op, String key, int indexDB, Object result) {
            log.info("[Redis] ==> option : [{}] | key : [{}] | indexDB : [{}] \n | Result : [{}]", op.getOpName(), key, indexDB, result.toString());
        }

        private static void LogReadResultError(RedisOption op, String key, int indexDB, Exception e) {
            log.error("[Redis] ==> option : [{}] | Key : [{}] | indexDB : [{}] \n | errorMsg : [{}]", op.getOpName(), key, indexDB, e.getMessage());
        }

        private static void LogWriteResultSuccess(RedisOption op, String key, Object value, int indexDB, Object result) {
            if (("OK").equals(result)) {
                result = "Success";
            } else {
                result = "Fail";
            }
            log.info("[Redis ({})] ==> option : [{}] | key : [{}] | value : [{}] | indexDB : [{}] \n ", result, op.getOpName(), key, value.toString(), indexDB);
        }

        private static void LogWriteResultError(RedisOption op, String key, Object value, int indexDB, Exception e) {
            log.error("[Redis] ==> option : [{}] | Key : [{}] | value : [{}] | indexDB : [{}] \n | errorMsg : [{}]", op.getOpName(), key, value.toString(), indexDB, e.getMessage());
        }

        private static void LogDelResultSuccess(RedisOption op, Long result, int indexDB, String... keys) {
            log.info("[Redis (OptionCount : {})] ==> option : [{}] | key : [{}] |  indexDB : [{}]", result, op.getOpName(), keys, indexDB);
        }

        private static void LogDelResultError(RedisOption op, Exception e, int indexDB, String... keys) {
            log.error("[Redis] ==> option : [{}] | Key : [{}] | indexDB : [{}] \n| errorMsg : [{}]", op.getOpName(), Arrays.asList(keys), indexDB, e.getMessage());
        }
    }
}
