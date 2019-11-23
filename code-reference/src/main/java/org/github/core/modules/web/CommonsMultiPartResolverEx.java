package org.github.core.modules.web;

import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * 扩展上传组件解决跨站脚本问题
 * Created by 章磊 on 2016/11/15.
 */
public class CommonsMultiPartResolverEx extends CommonsMultipartResolver {
    private boolean resolveLazily;
    @Override
    public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
        Assert.notNull(request, "Request must not be null");
        if(this.resolveLazily) {
            return new DefaultMultipartHttpServletRequestEx(request) {
                protected void initializeMultipart() {
                    MultipartParsingResult parsingResult = CommonsMultiPartResolverEx.this.parseRequest(request);
                    this.setMultipartFiles(parsingResult.getMultipartFiles());
                    this.setMultipartParameters(parsingResult.getMultipartParameters());
                    this.setMultipartParameterContentTypes(parsingResult.getMultipartParameterContentTypes());
                }
            };
        } else {
            MultipartParsingResult parsingResult = this.parseRequest(request);
            return new DefaultMultipartHttpServletRequestEx(request, parsingResult.getMultipartFiles(), parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
        }
    }
    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }
}
