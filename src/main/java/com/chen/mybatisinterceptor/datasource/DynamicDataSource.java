package com.chen.mybatisinterceptor.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: ChenHao
 * @Date: 2019/5/8
 * @Discription :
 **/
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);

    //int的原子操纵类，在多线程环境下可以安全的自增，初始值设置为 -1
    private AtomicInteger counter = new AtomicInteger(-1);

    //存放从库的key
    private List<Object> slaveDataSources = new ArrayList<>(0);

    @Override
    protected Object determineCurrentLookupKey() {

        Object key = null;
        if (DynamicDataSourceHolder.isMaster() || this.slaveDataSources.isEmpty()) {
            //如果当前请求是写请求，并且没有任何的从库，返回主库的key
            key = DynamicDataSourceHolder.MASTER;
        } else {
            //否则轮询从库的key
            key = this.getSlaveKey();
        }
        LOGGER.debug("动态数据源 dataSourceKey = {}", key);
        return key;
    }

    //AbstractRoutingDataSource 实现了 InitializingBean 接口，所以在属性注入完毕后会执行该方法
    //通过覆写该方法获，从 resolvedDataSources 中获取到所有的从库
    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Field field = ReflectionUtils.findField(AbstractRoutingDataSource.class, "resolvedDataSources");
        //resolvedDataSources 是 private 属性，设置暴力访问
        field.setAccessible(true);
        try {
            Map<Object, DataSource> resolvedDataSources = (Map<Object, DataSource>) field.get(this);
            for (Map.Entry<Object, DataSource> entry : resolvedDataSources.entrySet()) {
                if (DynamicDataSourceHolder.MASTER.equals(entry.getKey())) {
                    //如果数据源的key与master库的key相同，跳过
                    continue;
                }
                //添加 从库的key 到集合
                slaveDataSources.add(entry.getKey());
            }
        } catch (Exception e) {
            LOGGER.error("afterPropertiesSet error! ", e);
        }
    }

    //轮询从库key
    public Object getSlaveKey() {
        Integer index = counter.incrementAndGet() % this.slaveDataSources.size();
        if (counter.get() > 9999) {
            counter.set(-1);
        }
        return slaveDataSources.get(index);
    }
}