package com.github.core.authorization;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 授权器
 * 登录验证和权限验证通过实现这个类来完成
 *
 * @author 章磊
 */
public interface Authrizer {
    /**
     * 判断是否登录
     *
     * @param request
     * @param response
     * @return
     */
    boolean isLogin(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 获得有权限的功能
     *
     * @param authorizationInfo
     */
    void getFunction(AuthorizationInfo authorizationInfo);


    /**
     * 自动维护功能编号
     *
     * @param mkbh
     * @param gnbh
     * @param gnmc
     * @param gnfun
     * @param sjbh
     */
    void saveRep_Mkgn(String mkbh, String gnbh, String gnmc, String gnfun, String sjbh);

}
