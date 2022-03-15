package com.wang.fastfootstartredis.Redis;

import com.wang.fastfootstartredis.RocketMQ.RedisMessage;
import com.wang.fastfootstartredis.Util.JSONUtil;
import com.wang.fastfootstartredis.Util.RedisUtil;
import com.wang.fastfootstartredis.enums.RedisOption;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/18 21:54
 * @Description:
 */

public class AsyncRedis implements Serializable {

    @Autowired
    private RocketMQTemplate mqTemplate;

    private final int retryCount = 3;

    public String zrem(String key,Integer member){
        return zrem(key,Collections.singletonList(member));
    }

    public String zrem(String key,List<Integer> member){
        return zrem(key,member,0);
    }

    public String zrem(String key,List<Integer> member,int indexDB){
        String result = null;
        int count = 0;
        while (count < retryCount && !"0".equals(result)) {
            count++;
            result = String.valueOf(RedisUtil.zrem(key,member,indexDB));
        }
        if (count >= retryCount && "0".equals(result)) {
            RedisMessage message = buildMessage(key,member,indexDB,RedisOption.ZREM);
            sendMessage(message);
            result = "MQ-Retry";
        }
        return result;
    }

    public String hdel(String key,Integer field){
        return hdel(key, Collections.singletonList(field));
    }

    public String hdel(String key,List<Integer> field){
        return hdel(key,field,0);
    }

    public String hdel(String key,List<Integer> field,int indexDB){
        String result = null;
        int count = 0;
        while (count < retryCount && !"0".equals(result)) {
            count++;
            result = String.valueOf(RedisUtil.hdel(key,field));
        }
        if (count >= retryCount && "0".equals(result)) {
            RedisMessage message = buildMessage(key,field,indexDB,RedisOption.HDEL);
            sendMessage(message);
            result = "MQ-Retry";
        }
        return result;
    }

    public String hmset(String keu,List<String> field,List<Object> value){
        return hmset(keu,field,value,0);
    }

    public String hmset(String key,List<String> field,List<Object> value,int indexDB){
        String result = null;
        int count = 0;
        while (count < retryCount && !"0".equals(result)) {
            count++;
            result = String.valueOf(RedisUtil.hmset(key,field,value,indexDB));
        }
        if (count >= retryCount && "0".equals(result)) {
            RedisMessage message = buildMessage(key,field,value,indexDB,RedisOption.HMSET);
            sendMessage(message);
            result = "MQ-Retry";
        }
        return result;
    }

    public String hset(String key,String field,Object value){
        return hset(key,field,value,0);
    }

    public String hset(String key,String field,Object value,int indexDB){
        String result = null;
        int count = 0;
        while (count < retryCount && !"0".equals(result)) {
            count++;
            result = String.valueOf(RedisUtil.hset(key,field,value,indexDB));
        }
        if (count >= retryCount && "0".equals(result)) {
            RedisMessage message = buildMessage(key,field,indexDB,RedisOption.HSET);
            sendMessage(message);
            result = "MQ-Retry";
        }
        return result;
    }

    public String setex(String key, Object body, int seconds) {
        return setex(key, body, seconds, 0);
    }

    public String setex(String key, Object body, int seconds, int indexDB) {
        String result = null;
        int count = 0;
        while (count < retryCount && !"OK".equals(result)) {
            count++;
            result = RedisUtil.set(key, body);
        }
        if (count >= retryCount && !"OK".equals(result)) {
            RedisMessage message = buildMessage(key, body, indexDB, seconds, RedisOption.SETEX);
            sendMessage(message);
            result = "MQ-Retry";
        }
        return result;
    }

    public <T> String mset(List<String> keys, List<T> value) {
        return mset(keys, value, 0);
    }

    public <T> String mset(List<String> keys, List<T> value, int indexDB) {
        String result = null;
        int count = 0;
        while (count < retryCount && !"OK".equals(result)) {
            count++;
            result = RedisUtil.mset(keys, value, indexDB);
        }
        // todo 这部分批量set但是没有判断set失败的指令进行重试
        //      现有逻辑是第一个指令成功就判断全体成功，逻辑有问题(虽然出错概率不大)
        if (count >= retryCount && !"OK".equals(result)) {
            for (int i = 0; i < keys.size(); i++) {
                RedisMessage message = buildMessage(keys.get(i), value.get(i), indexDB, RedisOption.SET);
                sendMessage(message);
                result = "MQ-Retry";
            }
        }
        return result;
    }

    public String set(String key, Object body) {
        return set(key, body, 0);
    }

    public String set(String key, Object body, int indexDB) {
        String result = null;
        int count = 0;
        while (count < retryCount && !"OK".equals(result)) {
            count++;
            result = RedisUtil.set(key, body);
        }
        if (count >= retryCount && !"OK".equals(result)) {
            RedisMessage message = buildMessage(key, body, indexDB, RedisOption.SET);
            sendMessage(message);
            result = "MQ-Retry";
        }
        return result;
    }

    public String delGeo(String key,String memberNum){
        return delGeo(key,memberNum,0);
    }

    public String delGeo(String key,String memberNum,int indexDB){
        Long result = 0L;
        int count = 0;
        while(count < retryCount && result == 0){
            count++;
            result = RedisUtil.delGeo(key,memberNum);
        }
        if(count <= retryCount && result == 0){
            RedisMessage message = buildMessage(indexDB, RedisOption.DELGEO, key, memberNum);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String del(List<String> keys) {
        if(keys.size() == 0){
            return null;
        }
        return del(keys.toArray(new String[0]));
    }

    public String del(String... keys) {
        return del(0, keys);
    }

    public String del(int indexDB, String... keys) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.del(indexDB, keys);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(indexDB, RedisOption.DEL, keys);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String incr(String key) {
        return incr(key, 0);
    }

    public String incr(String key, int indexDB) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.incr(key, indexDB);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(key, indexDB, RedisOption.INCR);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String incrBy(String key, Long value, int indexDB) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.incrBy(key, value, indexDB);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(key, value, indexDB, RedisOption.INCRBY);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String decr(String key) {
        return decr(key, 0);
    }

    public String decr(String key, int indexDB) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.decr(key, indexDB);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(key, indexDB, RedisOption.DECR);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String decrBy(String key, Long value) {
        return decrBy(key, value, 0);
    }

    public String decrBy(String key, Long value, int indexDB) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.decrBy(key, value, indexDB);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(key, value, indexDB, RedisOption.DECRBY);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    public String expire(String key, int value, int indexDB) {
        Long result = 0L;
        int count = 0;
        while (count < retryCount && result == 0) {
            count++;
            result = RedisUtil.expire(key, value, indexDB);
        }
        if (count >= retryCount && result == 0) {
            RedisMessage message = buildMessage(key, value, indexDB, RedisOption.EXPIRE);
            sendMessage(message);
        }
        return result == 0L ? "MQ-Retry" : result.toString();
    }

    private void sendMessage(RedisMessage message) {
        String json = JSONUtil.toJsonString(message);
        // todo 后面可能考虑不同服务MQ重试需要放在不同的队列，所以可能需要抽取出配置Bean
        mqTemplate.convertAndSend("RedisOption", json);
    }

    private RedisMessage buildMessage(String key,int indexDB,RedisOption option,String memberNum){
        RedisMessage mq = new RedisMessage();
        mq.setKey(key);
        mq.setOption(option);
        mq.setIndexDB(indexDB);
        mq.setMemberNum(memberNum);
        return mq;
    }

    private RedisMessage buildMessage(String key, int indexDB, RedisOption option) {
        RedisMessage mq = new RedisMessage();
        mq.setKey(key);
        mq.setOption(option);
        mq.setIndexDB(indexDB);
        return mq;
    }

    private RedisMessage buildMessage(int indexDB, RedisOption option, String... keys) {
        RedisMessage mq = new RedisMessage();
        mq.setDelKeys(keys);
        mq.setOption(option);
        mq.setIndexDB(indexDB);
        return mq;
    }

    private RedisMessage buildMessage(String key, Object body, int indexDB, RedisOption option) {
        RedisMessage mq = new RedisMessage();
        mq.setKey(key);
        mq.setPayload(body);
        mq.setOption(option);
        mq.setIndexDB(indexDB);
        return mq;
    }

    private RedisMessage buildMessage(String key, Object body, int seconds, int indexDB, RedisOption option) {
        RedisMessage mq = new RedisMessage();
        mq.setKey(key);
        mq.setPayload(body);
        mq.setSeconds(seconds);
        mq.setOption(option);
        mq.setIndexDB(indexDB);
        return mq;
    }

    private RedisMessage buildMessage(String key,String field,Object value,int indexDB,RedisOption option){
        RedisMessage mq = new RedisMessage();
        mq.setKey(key);
        mq.setField(field);
        mq.setPayload(value);
        mq.setIndexDB(indexDB);
        mq.setPayload(option);
        return mq;
    }

    public RedisMessage buildMessage(String key,List<String> field,int indexDB,RedisOption option){
        RedisMessage mq = new RedisMessage();
        mq.setKey(key);
        mq.setFields(field);
        mq.setIndexDB(indexDB);
        mq.setOption(option);
        return mq;
    }

    public RedisMessage buildMessage(String key,List<String> field,List<Object> value,int indexDB,RedisOption option){
        RedisMessage mq = new RedisMessage();
        mq.setKey(key);
        mq.setPayloads(value);
        mq.setFields(field);
        mq.setIndexDB(indexDB);
        mq.setOption(option);
        return mq;
    }
}
