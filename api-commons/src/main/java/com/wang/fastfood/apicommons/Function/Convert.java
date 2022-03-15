package com.wang.fastfood.apicommons.Function;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/3/13 00:50
 * @Description:
 */

@FunctionalInterface
public interface Convert<T,S> {

    List<S> doConvert(List<T> list);

}
