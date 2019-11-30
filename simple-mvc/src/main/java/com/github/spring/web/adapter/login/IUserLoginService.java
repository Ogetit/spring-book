package com.github.spring.web.adapter.login;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件描述 用户登录服务
 *
 * @author ouyangjie
 * @Title: IUserLoginService
 * @ProjectName spring-book
 * @date 2019/11/30 2:53 PM
 */
public interface IUserLoginService {
    /**
     * 获取用户信息
     * @param request
     * @return
     */
    Object getUserInfo(final HttpServletRequest request);

    /**
     * 判断当前请求是否有权限
     * @param request
     * @return
     */
    boolean hasAuth(final HttpServletRequest request);
}
