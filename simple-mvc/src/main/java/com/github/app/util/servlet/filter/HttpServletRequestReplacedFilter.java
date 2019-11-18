package com.github.app.util.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.github.app.util.servlet.wapper.ServletRequestWrapper;

/**
 * 文件描述 替换HttpServletRequest工具类，用于HttpServletRequest后面日志里面多次读取
 *
 * @author ouyangjie
 * @Title: HttpServletRequestReplacedFilter
 */
public class HttpServletRequestReplacedFilter implements Filter {
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            requestWrapper = new ServletRequestWrapper((HttpServletRequest) request);
        }

        if (requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }
}