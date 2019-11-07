package com.github.spring.web.response;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件描述 response 处理工具类
 *
 * @author ouyangjie
 * @Title: WebRequestUtil
 */
public class WebResponseUtil {
    private static final Logger logger = LoggerFactory.getLogger(WebResponseUtil.class);

    public static void writeResponseBody(HttpServletResponse response, String jsonObject) throws IOException {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(jsonObject);
            out.flush();
            out.close();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e2) {
                    logger.error("关闭通道异常！", e2);
                }
            }
        }
    }
}
