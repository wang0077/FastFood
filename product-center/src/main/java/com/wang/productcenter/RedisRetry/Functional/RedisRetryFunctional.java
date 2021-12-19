package com.wang.productcenter.RedisRetry.Functional;

/**
 * @Auther: wAnG
 * @Date: 2021/12/20 02:55
 * @Description:
 */
@FunctionalInterface
public interface RedisRetryFunctional<T,R> {

    // todo 可能通过函数式接口改造Redis重试的大量if
    String retry(T key,R body);

}
