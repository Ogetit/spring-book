package com.github.core.ssologin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SsoLoginHandlerInterceptor extends HandlerInterceptorAdapter {
    // 5分钟失效，可以配置
    private int timeOutMin = 5;
    private SsoLogin ssoLogin;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        //免登录人id
        String ssouserid = StringUtils.trimToEmpty(request.getParameter("ssouserid"));
        if (StringUtils.isNotBlank(ssouserid)) {
            /*Ve_token t = new Ve_token();
            t.setUserid(ssouserid);
            Ve_token token = ve_tokenService.getEntityById(t);
            if (ssoLogin != null && token != null
                    && DateUtil.getTwoMin(new Date(), token.getCj_datetime()) < timeOutMin) {
                ssoLogin.login(ssouserid, request, response);
            }*/
        }
        return super.preHandle(request, response, handler);
    }

    public void setTimeOutMin(int timeOutMin) {
        this.timeOutMin = timeOutMin;
    }

    public void setSsoLogin(SsoLogin ssoLogin) {
        this.ssoLogin = ssoLogin;
    }
}
