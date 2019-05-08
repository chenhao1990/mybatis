package com.chen.mybatisinterceptor.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Author: ChenHao
 * @Date: 2019/5/8
 * @Discription :
 **/
@Order(-9999)        //它必须优先于业务层的所有切面方法之前执行
@Aspect
@Component
public class DaynamicDatasouceAop {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaynamicDatasouceAop.class);

    //切面定义，业务层的所有类的所有方法
    @Pointcut(value = "execution(* io.springcloud.service.*.*(..))")
    public void service() {
    }

    @Before("service()")
    public void beforService(JoinPoint joinPoint) throws NoSuchMethodException, SecurityException {

        Object target = joinPoint.getTarget();

        String methodName = joinPoint.getSignature().getName();

        Object[] args = joinPoint.getArgs();


        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();

        //目标方法
        Method method = null;

        method = target.getClass().getMethod(methodName, parameterTypes);

        if (method.isBridge()) {
            for (int i = 0; i < args.length; i++) {
                Class<?> genClazz = getSuperClassGenricType(target.getClass(), 0);
                if (args[i].getClass().isAssignableFrom(genClazz)) {
                    parameterTypes[i] = genClazz;
                }
            }
            method = target.getClass().getMethod(methodName, parameterTypes);
        }

        LOGGER.debug("当前事务方法  " + methodName);

        Transactional transactional = method.getAnnotation(Transactional.class);
        //未标识@Transactional注解，或者事务非只读，则标识主库
        if (transactional != null && transactional.readOnly()) {
            LOGGER.debug("动态数据源 - 读库");
            DynamicDataSourceHolder.markSlave();
        } else {
            LOGGER.debug("动态数据源 - 写库");
            DynamicDataSourceHolder.markMaster();
        }
    }

    public Class<?> getSuperClassGenricType(Class<?> clazz, int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class<?>) params[index];
    }
}

