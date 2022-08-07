package com.wang.authcenter.dao;

import com.wang.authcenter.entity.Authorization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Auther: wAnG
 * @Date: 2022/4/4 21:34
 * @Description:
 */

@Mapper
public interface AuthorizationDao {

    List<Authorization> getAuthorization();

}
