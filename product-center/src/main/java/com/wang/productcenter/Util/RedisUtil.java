package com.wang.productcenter.Util;

import com.wang.productcenter.enums.RedisOption;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;

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

    @Autowired
    public void setJedisPool(JedisPool jedisPool) {
        RedisUtil.jedisPool = jedisPool;
    }

    public void setRedisson(RedissonClient redisson) {
        RedisUtil.redisson = redisson;
    }

    public static Jedis getResource() {
        return jedisPool.getResource();
    }

    private static String Serialization(Object value) {
        return JSONUtil.toJsonString(value);
    }

    private static <T> T Deserialization(String json, Class<T> clazz) {
        return JSONUtil.parse(json, clazz);
    }

    public static void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
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
