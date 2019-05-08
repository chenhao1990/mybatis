package com.chen.mybatisinterceptor.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ChenHao
 * @Date: 2019/5/8
 * @Discription :
 **/
@ConfigurationProperties(prefix = "daynamic")
public class DynamicDataSourceConfig {

    private Map<String,DruidDataSource> datasources = new HashMap<>();

    public Map<String,DruidDataSource> getDatasources() {
        return datasources;
    }

    public void setDatasources(Map<String,DruidDataSource> datasources) {
        this.datasources = datasources;
    }
}