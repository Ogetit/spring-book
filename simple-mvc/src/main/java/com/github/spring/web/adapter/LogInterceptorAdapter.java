package com.github.spring.web.adapter;

import java.io.IOException;

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
        if (checkSessionUser(request, response)) {
            return super.preHandle(request, response, handler);
        } else {
            return false;
        }
    }

    /**
     * 检查 SessionUser
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean checkSessionUser(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final HttpSession session = request.getSession(true);
        Object loginUser = session.getAttribute(WebConst.USER_SESSION_KEY);
        // 游客可以访问
        if (null == loginUser) {
            return true;
        }
        // TODO：使用时候需要获取用户信息
        // 无权限页面
        String redirect403Url = "/error/403";
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
        return true;
    }
}
