package org.github.core.modules.web;

import org.apache.commons.lang.StringUtils;
import org.github.core.modules.mapper.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 直接输出内容的简便函数.
 */
public class ServletRender {
    private static final Logger logger = LoggerFactory.getLogger(ServletRender.class);
    // -- header 常量定义 --//
    private static final String ENCODING_PREFIX = "encoding";

    private static final String NOCACHE_PREFIX = "no-cache";

    private static final String ENCODING_DEFAULT = "UTF-8";

    private static final boolean NOCACHE_DEFAULT = true;


    // -- 绕过jsp/freemaker直接输出文本的函数 --//

    /**
     * 直接输出内容的简便函数.
     * <p>
     * eg. render("text/plain", "hello", "encoding:GBK"); render("text/plain",
     * "hello", "no-cache:false"); render("text/plain", "hello", "encoding:GBK",
     * "no-cache:false");
     *
     * @param headers 可变的header数组，目前接受的值为"encoding:"或"no-cache:",默认值分别为UTF-8和true.
     */
    public static void render(HttpServletResponse response, final String contentType, final String content,
                              final String... headers) {
        try {
            // 分析headers参数
            String encoding = ENCODING_DEFAULT;
            boolean noCache = NOCACHE_DEFAULT;
            for (String header : headers) {
                String headerName = StringUtils.substringBefore(header, ":");
                String headerValue = StringUtils.substringAfter(header, ":");

                if (StringUtils.equalsIgnoreCase(headerName, ENCODING_PREFIX)) {
                    encoding = headerValue;
                } else if (StringUtils.equalsIgnoreCase(headerName, NOCACHE_PREFIX)) {
                    noCache = Boolean.parseBoolean(headerValue);
                } else
                    throw new IllegalArgumentException(headerName + "不是一个合法的header类型");
            }

            // 设置headers参数
            String fullContentType = contentType + ";charset=" + encoding;
            response.setContentType(fullContentType);
            if (noCache) {
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
            }

            response.getWriter().write(content);
            response.getWriter().flush();

        } catch (IOException e) {
            logger.error("输出内容失败", e);
        }
    }

    /**
     * 直接输出文本.
     */
    public static void renderText(HttpServletResponse response, final String text, final String... headers) {
        render(response, MediaTypes.TEXT_PLAIN, text, headers);
    }

    /**
     * 直接输出HTML.
     */
    public static void renderHtml(HttpServletResponse response, final String html, final String... headers) {
        render(response, MediaTypes.TEXT_HTML, html, headers);
    }

    /**
     * 直接输出XML.
     */
    public static void renderXml(HttpServletResponse response, final String xml, final String... headers) {
        render(response, MediaTypes.TEXT_XML, xml, headers);
    }

    /**
     * 直接输出JSON.
     */
    public static void renderJson(HttpServletResponse response, final String jsonString, final String... headers) {
        render(response, MediaTypes.JSON, jsonString, headers);
    }

    /**
     * 直接输出JSON.
     */
    @SuppressWarnings("unchecked")
    public static void renderJson(HttpServletResponse response, final Map map, final String... headers) {
        String jsonString = JsonMapper.nonEmptyMapper().toJson(map);
        render(response, MediaTypes.JSON, jsonString, headers);
    }

    /**
     * 直接输出JSON.
     */
    public static void renderJson(HttpServletResponse response, final Object object, final String... headers) {
        String jsonString = JsonMapper.nonEmptyMapper().toJson(object);
        render(response, MediaTypes.JSON, jsonString, headers);
    }

    /**
     * 直接输出JSON.
     */
    public static void renderJson(HttpServletResponse response, final Object[] array, final String... headers) {
        String jsonString = JsonMapper.nonEmptyMapper().toJson(array);
        render(response, MediaTypes.JSON, jsonString, headers);
    }

    /**
     * 直接输出支持跨域Mashup的JSONP.
     *
     * @param callbackName callback函数名.
     * @param contentMap   Map对象,将被转化为json字符串.
     */
    @SuppressWarnings("unchecked")
    public static void renderJsonp(HttpServletResponse response, final String callbackName, final Map contentMap,
                                   final String... headers) {
        String jsonString = JsonMapper.nonEmptyMapper().toJson(contentMap);

        StringBuilder result = new StringBuilder().append(callbackName).append("(").append(jsonString).append(");");

        // 渲染Content-Type为javascript的返回内容,输出结果为javascript语句,
        // 如callback197("{content:'Hello World!!!'}");
        render(response, MediaTypes.JAVASCRIPT, result.toString(), headers);
    }
}
