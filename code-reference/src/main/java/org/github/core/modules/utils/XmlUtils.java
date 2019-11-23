package org.github.core.modules.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlUtils {

    private final static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    public static String xmlnode(String node, Object o) {
        if (o == null) {
            return "<" + node + "></" + node + ">";
        } else {
            if (o instanceof String) {
                String s = StringUtils.trimToEmpty((String) o);
                if ("".equals(s) || "null".equalsIgnoreCase(s)) {
                    return "<" + node + "></" + node + ">";
                }
                s = StrUtil.toXmlFormat(s);
                s = StrUtil.clearToXml(s);
                s = StringEscapeUtils.escapeSql(s);
//                采用统一的sql过滤表达式进行过滤。[
//                需要过滤掉的sql关键字有：
//                And、or、--、&&、--、||等。
//                开发人员参考一下网上比较全面的“java sql注入类过滤器”。
                String p="\\s+or\\s+";
                Pattern p1 = Pattern.compile(p, Pattern.CASE_INSENSITIVE);
                Matcher m1 = p1.matcher(s);
                s= m1.replaceAll("or");

                p = "\\s+and\\s+";
                p1 = Pattern.compile(p, Pattern.CASE_INSENSITIVE);
                m1 = p1.matcher(s);
                s = m1.replaceAll("and");

                return "<" + node + ">" + s + "</" + node + ">";
            } else if (o instanceof Double) {// double类型最多6位小数
                Double d = (Double) o;
                DecimalFormat df = new DecimalFormat("#.######");
                String num = df.format(d);
                return "<" + node + ">" + num + "</" + node + ">";
            } else {
                return "<" + node + ">" + o + "</" + node + ">";
            }
        }
    }

    /**
     * 单独获取xml中某个节点的值
     *
     * @param xml
     * @param node
     * @return
     */
    public static String getNodeValue(String xml, String node) {
        if (StringUtils.isBlank(xml)) {
            return null;
        }
        int p = xml.indexOf("<" + node + ">");
        if (p >= 0) {
            int e = xml.indexOf("</" + node + ">", p);
            if (e > p) {
                return xml.substring(p + ("<" + node + ">").length(), e);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String s="a OR NFVE asdfOr1=1asdfds";
        String p="(\\s+or\\s+)";
        Pattern p1 = Pattern.compile(p, Pattern.CASE_INSENSITIVE);
        Matcher m1 = p1.matcher(s);
        while(m1.find()) {
            String g =StringUtils.trimToEmpty(m1.group(0));
            s = m1.replaceAll(g);
            System.out.println(g);
        }
        System.out.println(s);


    }
}
