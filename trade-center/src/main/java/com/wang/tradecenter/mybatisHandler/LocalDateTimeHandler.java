package com.wang.tradecenter.mybatisHandler;

import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @Auther: wAnG
 * @Date: 2022/3/25 03:34
 * @Description:
 */


@Component
public class LocalDateTimeHandler extends LocalDateTimeTypeHandler {
    @Override
    public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        Object object = rs.getObject(columnName);
        if(object == null){
            return null;
        }
        return super.getResult(rs,columnName);
    }
}
