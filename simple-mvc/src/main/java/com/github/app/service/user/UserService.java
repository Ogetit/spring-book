package com.github.app.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.app.dao.user.UserDao;
import com.github.app.entity.user.User;

/**
 * 应用用户表 对应 Service 类
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    /**
     * 通用查询方法
     */
    public List<User> fetch(User entity) {
        return userDao.select(entity);
    }

    /**
     * 通用单表插入方法
     */
    public void insert(User entity) {
        userDao.insert(entity);
    }

    /**
     * 通用单表更新方法
     */
    public void update(User entity) {
        userDao.update(entity);
    }

    /**
     * 通用单表删除方法
     */
    public void delete(Integer id) {
        userDao.delete(id);
    }

    /**
     * 通用单表主键查询方法
     */
    public User fetchById(Integer id) {
        return userDao.fetchById(id);
    }
}