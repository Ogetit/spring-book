package com.github.app.service.auth;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.github.core.auth.AppUserDetail;
import com.github.core.auth.IUserSecurityService;

/**
 * 文件描述 获取用户信息
 *
 * @author ouyangjie
 * @Title: UserSecurityService
 * @ProjectName spring-book
 * @date 2019/11/30 3:14 PM
 */
@Service
public class UserSecurityService implements IUserSecurityService {
    @Override
    public AppUserDetail getUserInfo(String username, String password) {
        // 数据源可以从数据库或其他地方获取
        return new AppUserDetail(username, password);
    }

    @Override
    public Set<GrantedAuthority> authorities(String username) {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        // 此处可以从数据库取
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
        authorities.add(authority);
        return authorities;
    }
}
