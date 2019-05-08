package com.chen.mybatisinterceptor.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ChenHao
 * @Date: 2019/5/8
 * @Discription :
 **/
@Configuration
@EnableConfigurationProperties(DynamicDataSourceConfig.class)        //载入yml配置到配置类
public class DynamicDataSourceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceConfiguration.class);

    @Autowired
    private DynamicDataSourceConfig dynamicDataSourceConfig;

    @Bean
    public DynamicDataSource dynamicDataSource() throws SQLException {

        //定义数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        //定义数据源map
        Map<Object, Object> dataSources = new HashMap<>();

        for (Map.Entry<String, DruidDataSource> entry : dynamicDataSourceConfig.getDatasources().entrySet()) {

            String key = entry.getKey();

            DruidDataSource dataSource = entry.getValue();

            if (!key.equals(DynamicDataSourceHolder.MASTER)) {
                //如果是从库，则添加到map
                dataSources.put(key, dataSource);
            }

            //初始化数据源
            dataSource.init();

            LOGGER.info("数据源初始化:key={}", key);
        }

        //尝试从配置中获取到主库
        DruidDataSource masterDataSource = dynamicDataSourceConfig.getDatasources().get(DynamicDataSourceHolder.MASTER);

        if (masterDataSource == null) {
            throw new IllegalArgumentException("未定义,master库(" + DynamicDataSourceHolder.MASTER + ")");
        }

        //添加主库到map
        dataSources.put(DynamicDataSourceHolder.MASTER, masterDataSource);

        //设置动态数据源默认使用的库为主库
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        //设置数据源map
        dynamicDataSource.setTargetDataSources(dataSources);

        return dynamicDataSource;
    }
}