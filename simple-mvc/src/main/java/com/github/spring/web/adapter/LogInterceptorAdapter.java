package com.github.spring.web.adapter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.github.spring.web.adapter.login.IUserLoginService;

/**
 * 登陆拦截器
 */
public class LogInterceptorAdapter extends HandlerInterceptorAdapter {

    @Autowired
    private IUserLoginService userService;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, Object handler)
            throws Exception {
        if (checkSessionUser(request, response)) {
            return super.preHandle(request, response, handler);
        } else {
            return false;
        }
    }

    /**
     * 检查 Session
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean checkSessionUser(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        // 使用时候需要获取用户信息
        Object userInfo = userService.getUserInfo(request);
        // 无权限页面
        String redirect403Url = "/error/403";
        if (userInfo == null) {
            response.sendRedirect(redirect403Url);
            return false;
        }
        // 验证用户角色权限，按情况实现
        boolean hasAuth = userService.hasAuth(request);
        if (!hasAuth) {
            response.sendRedirect(redirect403Url);
            return false;
        }
        return true;
    }
}
