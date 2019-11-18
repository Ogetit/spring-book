package com.github.app.util.servlet.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 文件描述 request处理工具类
 *
 * @author ouyangjie
 * @Title: WebRequestUtil
 */
public class WebRequestUtil {
    private static final String POINT = ".";
    private static final String LEFT_PARENTHESIS = "[";
    private static final String RIGHT_PARENTHESIS = "]";
    private static final String PARENTHESIS = "[]";
    /**
     * json类型请求
     */
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    private static final Logger logger = LoggerFactory.getLogger(WebRequestUtil.class);

    private WebRequestUtil() {
    }

    /**
     * @Description 获取JSON字符串中的数据
     * @Param [request]
     * @Return java.lang.String
     */
    public static String getJSONParam(NativeWebRequest request) {
        try {
            HttpServletRequest httpServletRequest = request.getNativeRequest(HttpServletRequest.class);
            Assert.notNull(httpServletRequest, "当前请求获取失败！");
            return getJSONParam(httpServletRequest);
        } catch (Exception e) {
            logger.error("获取JSON字符串中的数据错误:" + e.getMessage(), e);
        }
        return "{}";
    }

    /**
     * @Description 获取JSON字符串中的数据
     * @Param [request]
     * @Return java.lang.String
     */
    public static String getJSONParam(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 1.获取输入流
            BufferedReader stramReader = new BufferedReader(new InputStreamReader(request.getInputStream()));

            // 2.写入数据到StringBuilder
            String line;
            while ((line = stramReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            logger.error("获取JSON字符串中的数据错误:" + e.getMessage(), e);
        }
        return stringBuilder.toString();
    }

    /**
     * @Description 将user.id=1这种形式的数据转为{user:{id:1}}形式
     * @Param [request]
     * @Return java.lang.String
     */
    public static String parseParamToJSONStr(HttpServletRequest request) {
        Map<String, String[]> conditionMap = request.getParameterMap();
        Iterator<String> names = CollectionUtils.toIterator(request.getParameterNames());
        return mapToJsonStr(conditionMap, names);
    }

    /**
     * @Description 将user.id=1这种形式的数据转为{user:{id:1}}形式
     * @Param [request]
     * @Return java.lang.String
     */
    public static String parseParamToJSONStr(NativeWebRequest request) {
        Map<String, String[]> conditionMap = request.getParameterMap();
        Iterator<String> names = request.getParameterNames();
        return mapToJsonStr(conditionMap, names);
    }

    /**
     * map转json数据
     *
     * @param conditionMap
     * @param names
     * @return
     */
    public static String mapToJsonStr(Map<String, String[]> conditionMap, Iterator<String> names) {
        JSONObject jsonObject = new JSONObject();
        while (names.hasNext()) {
            String name = names.next();
            String[] value = conditionMap.get(name);
            if (value != null) {
                parseHavePointParamToJSON(jsonObject, name, value);
            }
        }
        return jsonObject.toJSONString();
    }

    /**
     * @Description 处理带有点的参数
     * @Param [jsonObject, name, value]
     * @Return void
     */
    public static void parseHavePointParamToJSON(JSONObject jsonObject, String name, String[] value) {
        // 处理中括号
        if (name.contains(LEFT_PARENTHESIS)) {
            name = name.replace(PARENTHESIS, "")
                    .replace(LEFT_PARENTHESIS, POINT)
                    .replace(RIGHT_PARENTHESIS, "");
        }
        if (!name.contains(POINT)) {
            // 如果没有小数点，证明已经是最后一个了
            // 判断是否是已经设置过值
            if (value.length > 1) {
                // 如果是组数，则以数组的形式存放数据
                JSONArray jsonArray = new JSONArray();
                jsonArray.addAll(Arrays.asList(value));
                jsonObject.put(name, jsonArray);
            } else {
                jsonObject.put(name, value[0]);
            }
        } else {
            // 如果还有小数点
            int pointIndex = name.indexOf(POINT);
            String tempName = name.substring(0, pointIndex);
            String subName = name.substring(name.indexOf(POINT) + 1, name.length());
            JSONObject subJSONObject;
            // 判断是否已经有对应的属性了
            if (jsonObject.getJSONObject(tempName) == null) {
                // 如果没有
                subJSONObject = new JSONObject();
            } else {
                // 如果有
                subJSONObject = jsonObject.getJSONObject(tempName);
            }
            jsonObject.put(tempName, subJSONObject);
            // 递归处理
            parseHavePointParamToJSON(subJSONObject, subName, value);
        }
    }

    /**
     * 获取请求字符串信息
     * @param request
     */
    public static String getRequestInfoStr(HttpServletRequest request) {
        return getRequestInfoStr(request, true);
    }
    /**
     * 获取请求字符串信息
     * @param request
     */
    public static String getRequestInfoStr(HttpServletRequest request, boolean withReqBody) {
        StringBuilder logInfo = new StringBuilder();
        String queryString = request.getQueryString();
        String requestURI = request.getRequestURI();
        String contentType = request.getContentType();
        String method = request.getMethod();
        // 拼接请求地址
        logInfo.append("[url：\"").append(requestURI);
        // 拼接 GET 请求参数
        if (null != queryString) {
            logInfo.append("?").append(queryString);
        }
        logInfo.append("\"");
        // HEAD 信息
        logInfo.append("==>head:{")
                .append("\"Content-Type\":\"").append(contentType).append("\",")
                .append("\"Method\":\"").append(method).append("\"")
                .append("}");
        // 请求题数据转json字符串展示
        if (withReqBody) {
            String jsonStr;
            if (!StringUtils.isEmpty(contentType) && contentType.contains(CONTENT_TYPE_JSON)) {
                jsonStr = WebRequestUtil.getJSONParam(request);
            } else {
                jsonStr = WebRequestUtil.parseParamToJSONStr(request);
            }
            logInfo.append("==>params:").append(jsonStr).append("]");
        } else {
            logInfo.append("]");
        }
        return logInfo.toString();
    }
}
