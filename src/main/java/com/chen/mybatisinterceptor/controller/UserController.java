package com.chen.mybatisinterceptor.controller;

import com.chen.mybatisinterceptor.pojo.User;
import com.chen.mybatisinterceptor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: ChenHao
 * @Date: 2019/5/6
 * @Discription :
 **/
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("user/getUsers")
    public List<User> getUsers(int pageSize,int pageNum){
        return userService.selectUsers(pageSize,pageNum);
    }
}
