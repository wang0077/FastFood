package com.wang.productcenter.Interceptor;

import com.wang.productcenter.config.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.*;

/**
 * @Auther: wAnG
 * @Date: 2021/11/27 03:02
 * @Description:
 */

@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = { Statement.class })})
@Component
public class SqlCostInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();

        long startTime = System.currentTimeMillis();
        long sqlCost = 0;
        StatementHandler statementHandler = (StatementHandler)target;
        List<Object> resultList = null;
        try {
            Object proceed = invocation.proceed();
            long endTime = System.currentTimeMillis();
            sqlCost = endTime - startTime;
            resultList = Collections.singletonList(proceed);
            return proceed;
        } finally {
            printSqlLog(startTime, statementHandler,resultList,sqlCost);
        }
    }

    private void printSqlLog(long startTime, StatementHandler statementHandler,List<Object> resultList,Long sqlCost) {
        BoundSql boundSql = statementHandler.getBoundSql();
        Object parameterObject = boundSql.getParameterObject();
        String sql = boundSql.getSql();
        if(parameterObject != null){
            sql = getFllSql(boundSql, parameterObject);
        }
        int total = resultList == null ? 0 : resultList.size();
        sql = sql.replace("\n", " ").replaceAll("\\s{1,}", " ");
        log.info("[MySQL (totalTime : {}ms)] ==> total : [{}]  \n>>> SQL : [{}] \n>>> Result : [{}]",sqlCost,total,sql,resultList);
    }

    public String getFllSql(BoundSql boundSql, Object parameterObject) {
        SqlSessionFactory bean = SpringContext.getBean(SqlSessionFactory.class);

        Configuration configuration = bean.getConfiguration();

        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        List<Object> paramList = new ArrayList<>();

        for (ParameterMapping parameterMapping : parameterMappings) {
            if (parameterMapping.getMode() == ParameterMode.OUT) {
                continue;
            }
            Object value = null;
            String propertyName = parameterMapping.getProperty();
            if (boundSql.hasAdditionalParameter(propertyName)) {
                value = boundSql.getAdditionalParameter(propertyName);
            } else if (parameterObject == null) {
                value = null;
            } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                value = parameterObject;
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                value = metaObject.getValue(propertyName);
            }
            paramList.add(value);
        }
        String sql = boundSql.getSql();
        for (Object o : paramList) {
            sql = sql.replaceFirst("\\?", formatParamValue(o));
        }
        return sql;
    }

    private String formatParamValue(Object paramValue) {
        if (paramValue == null) {
            return "null";
        }
        if (paramValue instanceof String) {
            paramValue =  "'" + paramValue + "'";
        }
        if (paramValue instanceof Date) {
            paramValue =  "'" + paramValue + "'";
        }
        return paramValue.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
