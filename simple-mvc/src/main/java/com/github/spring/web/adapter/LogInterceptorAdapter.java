package com.github.spring.web.adapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.github.spring.web.common.WebConst;

/**
 * 登陆拦截器
 */
public class LogInterceptorAdapter extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, Object handler)
            throws Exception {
        final HttpSession session = request.getSession(true);
        // TODO：使用时候需要获取用户信息
        Object loginUser = session.getAttribute(WebConst.USER_SESSION_KEY);
        // redis 用户一个小时过期一次
        String redirect403Url = "/error/403.jsp";
        if (loginUser == null) {
            response.sendRedirect(redirect403Url);
            return false;
        }
        // TODO：验证用户角色权限，按情况实现
        boolean hasAuth = true;
        if (!hasAuth) {
            response.sendRedirect(redirect403Url);
            return false;
        }
        return super.preHandle(request, response, handler);
    }
}
