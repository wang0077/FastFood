package com.wang.fastfood.apicommons.Util;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.Function.Convert;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.entity.common.RedisPageInfo;
import com.wang.fastfood.apicommons.exception.StartPageException;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/9 02:21
 * @Description: 分页工具类
 */

public class PageUtils {

    public static <T> RedisPageInfo<T> startRedisPage(T Bean){
        RedisPageInfo<T> pageInfo = null;
        try {
            if(Bean instanceof Page){
                pageInfo = new RedisPageInfo<>();
                Page page = (Page) Bean;
                if(!page.IsPage()){
                    return pageInfo;
                }
                pageInfo.setPageNum(page.getPageNum());
                pageInfo.setPageSize(page.getPageSize());
                pageInfo.setIsPage(true);
            }else {
                throw new StartPageException();
            }
        }catch (StartPageException e){
            e.printStackTrace();
        }
        return pageInfo;
    }

    /**
     * 使用PageHelper开启分页，直接传入对应的Bean会自动判断是否需要进行分页
     */
    public static<T> void startPage(T Bean){
        try {
            if(Bean instanceof Page){
                Page page = (Page) Bean;
                if(!page.IsPage()){
                    return;
                }
                PageHelper.startPage(page.getPageNum(),page.getPageSize());
                if(page.getOrderBy() != null){
                    PageHelper.orderBy(page.getOrderBy());
                }
            }else {
                throw new StartPageException();
            }
        }catch (StartPageException e){
            e.printStackTrace();
        }
    }


    /**
     * 绕过PageHelper对带有分页数据的list进行操作会破坏分页数据的设计问题(or BUG?)
     * 将pageHelper带有分页数据的List进行转换一个新的list并携带之前的分页数据
     * @param sourceList PageHelper查询出来的Page类型转换为List的list
     * @param destList 需要转换的list
     * @return 返回一个PageInfo，内部包装的分页数据和list，可从PageInfo中获取list
     */
    public static<T,S> PageInfo<S> getPageInfo(List<T> sourceList,List<S> destList){
        PageInfo<T> source = new PageInfo<>(sourceList);
        return getPageInfo(source,destList);
    }

    public static <T,S> PageInfo<S> getPageInfo(PageInfo<T> source,List<S> destList){
        if(source != null){
            com.github.pagehelper.Page<S> dest =
                    new com.github.pagehelper.Page<>(source.getPageNum(), source.getPageSize());
            dest.setTotal(source.getTotal());
            PageInfo<S> result = new PageInfo<>(dest);
            result.setList(destList);
            return result;
        }
        return null;
    }

    public static <T,S> PageInfo<S> getPageInfo(RedisPageInfo<T> source, Convert<T,S> convert){
        if(source != null){
            com.github.pagehelper.Page<S> dest =
                    new com.github.pagehelper.Page<>(source.getPageNum(), source.getPageSize());
            dest.setTotal(source.getTotal());
            PageInfo<S> result = new PageInfo<>(dest);
            result.setList(convert.doConvert(source.getList()));
            return result;
        }
        return null;
    }

    public static <T> PageInfo<T> getPageInfo(RedisPageInfo<T> source){
        if(source != null){
            com.github.pagehelper.Page<T> dest =
                    new com.github.pagehelper.Page<>(source.getPageNum(), source.getPageSize());
            dest.setTotal(source.getTotal());
            PageInfo<T> result = new PageInfo<>(dest);
            result.setList(source.getList());
            return result;
        }
        return null;
    }

}
