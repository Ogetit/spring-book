package com.github.core.authorization;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 功能日志
 * @author 章磊
 *
 */
public interface FunctionLogHandler {
	/**
	 * 创建日志对象
	 * @param request
	 * @param response
	 * @param handler controller类
	 */
	public void createLog(HttpServletRequest request, HttpServletResponse response, Object handler, String moduleNo, String functionNo, Map function);
	/**
	 * 提交日志
	 */
	public void submit();
}
