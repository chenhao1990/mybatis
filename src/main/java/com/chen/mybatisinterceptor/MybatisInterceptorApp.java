package com.chen.mybatisinterceptor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chen.mybatisinterceptor.mapper")
public class MybatisInterceptorApp {

    public static void main(String[] args) {
        SpringApplication.run(MybatisInterceptorApp.class, args);
    }

}
