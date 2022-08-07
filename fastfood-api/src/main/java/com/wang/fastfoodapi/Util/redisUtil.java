package com.wang.fastfoodapi.Util;

import com.github.pagehelper.PageInfo;
import com.wang.fastfoodapi.enums.RedisOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/4/5 01:18
 * @Description:
 */

@Component
@Slf4j
public class redisUtil {

    private static JedisPool jedisPool;

    @Autowired
    public void setJedisPool(JedisPool jedisPool) {
        redisUtil.jedisPool = jedisPool;
    }

    private static Jedis getResource() {
        return jedisPool.getResource();
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

    public static Map<String,String> hgetAll(String key){
        long totalTime;
        Jedis jedis = null;
        Map<String,String> result = null;
        try {
            long startTime = System.currentTimeMillis();
            jedis = getResource();
            result = jedis.hgetAll(key);
            long endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
        return result;
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

    public static <T> String hmset(String key,List<String> field,List<T> value){
        return hmset(key,field,value,0);
    }

    public static <T> String hmset(String key,List<String> field,List<T> value,int indexDB){
        long totalTime;
        Jedis jedis = null;

        // 先对Object进行序列化
        List<String> valueJson = value.stream()
                .map(redisUtil::Serialization)
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
