package com.github.core.auth;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

public interface IUserSecurityService {
    /**
     * 通过用户名和密码获取用户信息
     *
     * @param username
     * @param password
     *
     * @return
     */
    AppUserDetail getUserInfo(String username, String password);

    /**
     * 获取用户角色权限信息
     * @param username
     * @return
     */
    Set<GrantedAuthority> authorities(String username);
}
