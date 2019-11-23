package org.github.core.modules.web;

import org.github.core.modules.antisamy.AntiSamyFilter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 扩展上传组件解决跨站脚本问题
 * Created by  章磊 on 2016/11/15.
 */
public class DefaultMultipartHttpServletRequestEx extends DefaultMultipartHttpServletRequest {
    public DefaultMultipartHttpServletRequestEx(HttpServletRequest request, MultiValueMap<String, MultipartFile> mpFiles, Map<String, String[]> mpParams, Map<String, String> mpParamContentTypes) {
        super(request, mpFiles, mpParams, mpParamContentTypes);
    }
    public DefaultMultipartHttpServletRequestEx(HttpServletRequest request) {
        super(request);
    }
    @Override
    public String getParameter(String name) {
        String[] values = (String[])this.getMultipartParameters().get(name);
        return values != null?(values.length > 0? AntiSamyFilter.CleanServletRequest.filterString(values[0]):null):super.getParameter(name);
    }
    @Override
    public String[] getParameterValues(String name) {
        String[] values = (String[])this.getMultipartParameters().get(name);
        List<String> newValues = new ArrayList<String>(values.length);
        for (String value : values) {
            newValues.add(AntiSamyFilter.CleanServletRequest.filterString(value));
        }
        return newValues != null?newValues.toArray(new String[newValues.size()]):super.getParameterValues(name);
    }
}
