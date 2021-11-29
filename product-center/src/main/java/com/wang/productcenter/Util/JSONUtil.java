package com.wang.productcenter.Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

/**
 * @Auther: wAnG
 * @Date: 2021/11/29 17:20
 * @Description:
 */

@Slf4j
public class JSONUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // 对于空的对象转json的时候不抛出错误
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    /**
     * 序列化，将对象转化为json字符串
     *
     */
    public static String toJsonString(Object data) {
        if (data == null) {
            return null;
        }

        String json = null;
        try {
            json = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("[JackJSON] ==> [{}] toJsonString error：{{}}", data.getClass().getSimpleName(), e);
        }
        return json;
    }

//    public static <T> T parse(@NonNull String json){
//        mapper.
//    }
    /**
     * 反序列化，将json字符串转化为对象
     *
     */
    public static <T> T parse(@NonNull String json, Class<T> clazz) {
        T t = null;
        try {
            t = mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("[JackJSON] ==> parse json [{}] to class [{}] error：{{}}", json, clazz.getSimpleName(), e);
        }
        return t;
    }
}
