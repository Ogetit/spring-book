package com.github.app.service.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.github.spring.web.adapter.login.IUserLoginService;
import com.github.spring.web.common.WebConst;

/**
 * 文件描述 用户登录Service
 *
 * @author ouyangjie
 * @Title: UserLoginService
 * @ProjectName spring-book
 * @date 2019/11/30 3:05 PM
 */
@Service
public class UserLoginService implements IUserLoginService {

    @Override
    public Object getUserInfo(HttpServletRequest request) {
        final HttpSession session = request.getSession(true);
        Object userInfo = session.getAttribute(WebConst.USER_SESSION_KEY);
        // 游客也能访问
        return userInfo == null ? new Object() : userInfo;
    }

    @Override
    public boolean hasAuth(HttpServletRequest request) {
        // 总有权限
        return true;
    }
}
