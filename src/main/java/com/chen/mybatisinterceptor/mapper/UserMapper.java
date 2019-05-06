package com.chen.mybatisinterceptor.mapper;

import com.chen.mybatisinterceptor.pojo.User;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @Author: ChenHao
 * @Date: 2019/5/6
 * @Discription :
 **/
public interface UserMapper {

List<User> getUsers(RowBounds rowBounds);

}
