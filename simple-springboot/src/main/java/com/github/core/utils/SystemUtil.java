package com.github.core.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.DigestUtils;

public class SystemUtil {
    /**
     * 获取客户端IP
     *
     * @param request the request
     *
     * @return IP remote ip
     */
    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String md5(String password) {
        if (password == null) {
            return null;
        }
        String encode = "UTF-8";
        try {
            return DigestUtils.md5DigestAsHex(password.getBytes(encode));
        } catch (Exception e) {
            return null;
        }
    }
}
