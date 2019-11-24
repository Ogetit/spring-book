package com.github.service;

import com.github.entity.AppUserDetail;

public interface IUserService {

    /**
     * 检查用户名是否已经被用过
     *
     * @param name
     *
     * @return
     */
    public boolean checkNameUsed(String name);

    /**
     * 通过用户名和密码获取用户信息
     *
     * @param name
     * @param password
     *
     * @return
     */
    public AppUserDetail getByNameAndPassword(String name, String password);
}
