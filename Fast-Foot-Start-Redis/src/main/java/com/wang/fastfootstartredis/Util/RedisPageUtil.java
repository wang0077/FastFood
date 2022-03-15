package com.wang.fastfootstartredis.Util;

import com.wang.fastfood.apicommons.Util.ReflectionUtil;
import com.wang.fastfood.apicommons.entity.common.RedisPageInfo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Auther: wAnG
 * @Date: 2022/3/12 16:21
 * @Description: Redis数据分页工具
 */

public class RedisPageUtil {

    public static <T> void computeRedisPageInfo(RedisPageInfo<T> pageInfo,String targetZset){
        if(!pageInfo.isPage()){
            return;
        }
        Long count = RedisUtil.zcard(targetZset);
        pageInfo.setTotal(count);
        computePageNumber(pageInfo);
    }

    private static <T> void computePageNumber(RedisPageInfo<T> pageInfo){
        int start = (pageInfo.getPageNum() - 1 ) * pageInfo.getPageSize();
        pageInfo.setStart(start);
        pageInfo.setEnd(start + pageInfo.getPageSize() - 1);
    }

    public static <T> void getPageData(RedisPageInfo<T> pageInfo,String setName,String hashName,Class<T> clazz){
        List<Integer> zSetIds = null;
        if(pageInfo.isPage()){
            zSetIds = RedisUtil.zrange(setName, pageInfo.getStart(), pageInfo.getEnd());
        }else {
            zSetIds = RedisUtil.zrange(setName, 0, -1);
        }
        pageInfo.setSetIds(zSetIds);
        List<T> dataList = RedisUtil.hmget(hashName
                , zSetIds.stream().map(String::valueOf).collect(Collectors.toList())
                , clazz);
        dataList = dataList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        pageInfo.setList(dataList);

        // 所有数据命中直接返回
        if(dataList.size() == zSetIds.size()){
            return;
        }

        // 查找未命中的数据
        List<Integer> hashIds = dataList.stream()
                .map(t -> (Integer) ReflectionUtil.getFieldValue(clazz, "id", t))
                .collect(Collectors.toList());

        List<Integer> missIds = zSetIds.stream()
                .filter(id -> !hashIds.contains(id))
                .collect(Collectors.toList());

        pageInfo.setExistMiss(true);
        pageInfo.setMissIds(missIds);
    }


}
