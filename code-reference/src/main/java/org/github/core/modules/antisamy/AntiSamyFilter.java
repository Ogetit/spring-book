package org.github.core.modules.antisamy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.github.core.modules.utils.StrUtil;
import org.github.core.modules.utils.WebUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet filter that checks all request parameters for potential XSS attacks.
 *
 * @author barry pitman
 * @since 2011/04/12 5:13 PM
 */
public class AntiSamyFilter implements Filter {
    protected static final Logger logger = LoggerFactory.getLogger(AntiSamyFilter.class);
    private String[] excludeUrl;//不安全验证的url
    /**
     * AntiSamy is unfortunately not immutable, but is threadsafe if we only call
     * {@link AntiSamy#scan(String taintedHTML, int scanType)}
     */
    private static AntiSamy antiSamy;

    public AntiSamyFilter() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            if (excludeUrl != null) {
                for (String url : excludeUrl) {
                    //如果请求的URL是排除的，就不做安全验证
                    if (((HttpServletRequest) request).getRequestURI().indexOf(url) > -1) {
                        chain.doFilter(request, response);
                        return;
                    }
                }
            }
            CleanServletRequest cleanRequest = new CleanServletRequest((HttpServletRequest) request, antiSamy);
            chain.doFilter(cleanRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String name = filterConfig.getInitParameter("policy");
        String excludeUrlStr = filterConfig.getInitParameter("exclude-url");
        if (StringUtils.isNotBlank(excludeUrlStr)) {
            excludeUrl = excludeUrlStr.split(",");
            logger.info("antisamy exclude-url: " + excludeUrlStr);
        }
        logger.info("antisamy 加载安全策略文件: " + name);

        try {
            Policy policy = Policy.getInstance(WebUtils.getAbsoluteFile(name));
            antiSamy = new AntiSamy(policy);
        } catch (PolicyException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * Wrapper for a {@link HttpServletRequest} that returns 'safe' parameter values by
     * passing the raw request parameters through the anti-samy filter. Should be private
     */
    public static class CleanServletRequest extends HttpServletRequestWrapper {

        private static AntiSamy antiSamy;

        private CleanServletRequest(HttpServletRequest request, AntiSamy antiSamy) {
            super(request);
            CleanServletRequest.antiSamy = antiSamy;
        }

        /**
         * overriding getParameter functions in {@link ServletRequestWrapper}
         */
        @Override
        public String[] getParameterValues(String name) {
            CleanResults cr = null;
            try {
                cr = antiSamy.scan(name, AntiSamy.DOM);
                if (cr.getNumberOfErrors() > 0) {
                    logger.warn("antisamy 从请求的name中发现输入有问题的字符: " + cr.getErrorMessages());
                    throw new RuntimeException("antisamy 从请求的name中发现输入有问题的字符: " + cr.getErrorMessages());
                }
            } catch (ScanException e) {
                e.printStackTrace();
            } catch (PolicyException e) {
                e.printStackTrace();
            }

            String[] originalValues = super.getParameterValues(name);
            if (originalValues == null) {
                return null;
            }
            List<String> newValues = new ArrayList<String>(originalValues.length);
            for (String value : originalValues) {
                newValues.add(filterString(value));
            }
            return newValues.toArray(new String[newValues.size()]);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Map getParameterMap() {
            Map<String, String[]> originalMap = super.getParameterMap();
            Map<String, String[]> filteredMap = new ConcurrentHashMap<String, String[]>(originalMap.size());
            for (String name : originalMap.keySet()) {
                filteredMap.put(name, getParameterValues(name));
            }
            return Collections.unmodifiableMap(filteredMap);
        }

        @Override
        public String getParameter(String name) {
            CleanResults cr = null;
            try {
                cr = antiSamy.scan(name, AntiSamy.DOM);
                if (cr.getNumberOfErrors() > 0) {
                    logger.warn("antisamy 从请求的name中发现输入有问题的字符: " + cr.getErrorMessages());
                    throw new RuntimeException("antisamy 从请求的name中发现输入有问题的字符: " + cr.getErrorMessages());
                }
            } catch (ScanException e) {
                e.printStackTrace();
            } catch (PolicyException e) {
                e.printStackTrace();
            }
            String potentiallyDirtyParameter = super.getParameter(name);
            return filterString(potentiallyDirtyParameter);
        }

        /**
         * This is only here so we can see what the original parameters were, you should delete this method!
         *
         * @return original unwrapped request
         */
        @Deprecated
        public HttpServletRequest getOriginalRequest() {
            return (HttpServletRequest) super.getRequest();
        }

        /**
         * @param potentiallyDirtyParameter string to be cleaned
         *
         * @return a clean version of the same string
         */
        public static String filterString(String potentiallyDirtyParameter) {
            if (potentiallyDirtyParameter == null) {
                return null;
            }
            return StringEscapeUtils.escapeSql(StrUtil.clearHtmlSimple(potentiallyDirtyParameter.trim()));

            //            try {
            //                CleanResults cr = antiSamy.scan(potentiallyDirtyParameter, AntiSamy.DOM);
            //                if (cr.getNumberOfErrors() > 0) {
            //                	logger.warn("antisamy 从请求中发现输入有问题的字符: " + cr.getErrorMessages());
            //                }
            //                return StringEscapeUtils.escapeSql(VeStr.clearHtmlSimple(cr.getCleanHTML()));
            //                //return cr.getCleanHTML();
            //            } catch (Exception e) {
            //                throw new IllegalStateException(e.getMessage(), e);
            //            }
        }
    }
}