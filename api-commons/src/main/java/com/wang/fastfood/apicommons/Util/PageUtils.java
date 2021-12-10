package com.wang.fastfood.apicommons.Util;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.fastfood.apicommons.entity.common.Page;
import com.wang.fastfood.apicommons.exception.StartPageException;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2021/12/9 02:21
 * @Description: 分页工具类
 */

public class PageUtils {

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

    public static<T> PageInfo<T> convertPage(List<T> Bean){
        return new PageInfo<T>(Bean);
    }

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

}