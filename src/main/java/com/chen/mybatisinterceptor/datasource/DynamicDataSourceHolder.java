package com.chen.mybatisinterceptor.datasource;

/**
 * @Author: ChenHao
 * @Date: 2019/5/8
 * @Discription :
 **/
public class DynamicDataSourceHolder {
    //定义主库的key
    public static final String MASTER = "master";

    private static final String SLAVE = "slave";

    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private static void putDataSourceKey(String key) {
        HOLDER.set(key);
    }

    public static String getDataSourceKey() {
        return HOLDER.get();
    }

    //标记为主库
    public static void markMaster() {
        putDataSourceKey(MASTER);
    }

    //标签为从库
    public static void markSlave() {
        putDataSourceKey(SLAVE);
    }

    //判断是否是主库
    public static boolean isMaster() {
        return MASTER.equals(HOLDER.get());
    }
}
