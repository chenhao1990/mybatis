package com.chen.mybatisinterceptor.service;

import com.chen.mybatisinterceptor.mapper.UserMapper;
import com.chen.mybatisinterceptor.pojo.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: ChenHao
 * @Date: 2019/5/6
 * @Discription :
 **/
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public List<User> selectUsers(int pageSize, int pageNum) {
        RowBounds rowBounds = new RowBounds(pageSize, pageNum);
        return userMapper.getUsers(rowBounds);
    }

}
