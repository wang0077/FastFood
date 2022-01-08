package com.wang.fastfoodapi.Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

    public static <T> T parse(InputStream src, Class<T> clazz) {
        T t = null;
        try {
            t = mapper.readValue(src, clazz);
        } catch (IOException e) {
            log.error("[JackJSON] ==> parse to class [{}] error：{{}}", clazz.getSimpleName(), e);
            return null;
        }
        return t;
    }

    /**
     * 反序列化，将json字符串转化为List<对象>
     *
     */
    public static <T> List<T> parseToList(@NonNull String json, Class<T> clazz) {
        List<T> t = null;
        try {
            t = mapper.readValue(json, getCollectionType(List.class,clazz));
        } catch (Exception e) {
            log.error("[JackJSON] ==> parse json [{}] to class [{}] error：{{}}", json, clazz.getSimpleName(), e);
        }
        return t;
    }

    /**
     * 反序列化，将json字符串转化为PageInfo<对象>
     *
     */
    public static <T> PageInfo<T> parseToPageInfo(@NonNull String json, Class<T> clazz) {
        PageInfo<T> t = null;
        try {
            t = mapper.readValue(json, getCollectionType(PageInfo.class,clazz));
        } catch (Exception e) {
            log.error("[JackJSON] ==> parse json [{}] to class [{}] error：{{}}", json, clazz.getSimpleName(), e);
        }
        return t;
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return JSONUtil.mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
