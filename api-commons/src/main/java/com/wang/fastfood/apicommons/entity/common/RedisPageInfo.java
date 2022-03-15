package com.wang.fastfood.apicommons.entity.common;

import lombok.Data;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/12 16:24
 * @Description:
 */

@Data
public class RedisPageInfo<T> {
    //当前页
    private int pageNum;

    //每页的数量
    private int pageSize;

    //当前页的数量
    private int size;

    // 总数
    private long total;

    //结果集
    protected List<T> list;

    // Zset维护的ID列表
    private transient List<Integer> setIds;

    // 开始区间
    private transient int start;

    // 结束区间
    private transient int end;

    // 是否开启分页
    private transient boolean isPage = false;

    // 是否存在未命中数据
    private transient boolean existMiss = false;

    // 未命中的Id
    private transient List<Integer> MissIds;


    public void setIsPage(boolean isPage){
        this.isPage = isPage;
    }

}
