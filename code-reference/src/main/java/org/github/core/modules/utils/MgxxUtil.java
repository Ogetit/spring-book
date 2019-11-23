package org.github.core.modules.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 敏感信息格式化工具
 * <p/>
 * Created by wangkai on 2017/7/20.
 */
public class MgxxUtil {
    private static final Map<Integer, String> TYPE_MAP = new HashMap<Integer, String>();

    static {
        TYPE_MAP.put(1, "ID");
        TYPE_MAP.put(2, "PASSPORT");
        TYPE_MAP.put(3, "EMAIL");
        TYPE_MAP.put(4, "PHONE");
        TYPE_MAP.put(5, "BANKCARD");
        TYPE_MAP.put(6, "ACCOUNT");
    }

    /**
     * 格式化敏感信息
     *
     * @param src  被格式化的字符串
     * @param type 信息类型
     *             <br/>
     *             <p>1:身份证(包含护照)</p>
     *             <p>2:护照</p>
     *             <p>3:邮箱</p>
     *             <p>4:手机号或固定电话</p>
     *             <p>5:银行卡</p>
     *             <p>6:帐号</p>
     * @return 出现异常时，返回原字符串
     */
    public static String format(String src, int type) {
        return format(src, TYPE_MAP.get(type));
    }

    /**
     * 格式化敏感信息
     *
     * @param src  被格式化的字符串
     * @param type 信息类型
     *             <br/>
     *             <p>ID:身份证(包含护照)</p>
     *             <p>PASSPORT:护照</p>
     *             <p>EMAIL:邮箱</p>
     *             <p>MOBILE:手机号或固定电话</p>
     *             <p>BANKCARD:银行卡</p>
     *             <p>ACCOUNT:帐号</p>
     * @return
     */
    public static String format(String src, String type) {
        try {
            return _format(src, type);
        } catch (Exception e) {
            return src;
        }
    }

    /**
     * 根据type格式化指定的字符串
     * <p/>
     * (1)ID:身份证号码前6后2正常显示，中间以*显示。
     * (2)PASSPORT:护照后4位以*显示。如：420821**********
     * (2)PHONE:电话号码前3后4正常显示，中间以*显示。如：186****0990
     * (3)BANKCARD:银行卡号前4后4正常显示，中间以*显示。如：6225 **** **** 3252
     * (4)EMAIL:邮箱@之前的部分，前2位正常显示，后面以*显示；@之后的部分正常显示。如：ve****@sina.com
     * (5)ACCOUNT:支付宝等账号，如果是手机号，则以手机号码规则显示；如果是邮箱，则按邮箱规则显示。其他账号信息后4位*隐藏显示。
     *
     * @param src
     * @param type
     * @return
     */
    private static String _format(String src, String type) throws Exception {
        if (_isBlank(src) || _isBlank(type)) {
            throw new Exception("参数错误.");
        }
        if ("ID".equalsIgnoreCase(type)) {
            if (!isID(src)) {
                type = "PASSPORT";
            }
        }
        if ("ACCOUNT".equalsIgnoreCase(type)) {
            if (isValidPhone(src)) {
                type = "phone";
            }
            if (isEmail(src)) {
                type = "email";
            }
        }

        switch (type.toUpperCase()) {
            case "ID":
                return _format(src, 6, src.length() - 2);
            case "PASSPORT":
                return _format(src, src.length() - 4, src.length());
            case "EMAIL":
                return _format(src, 2, _indexOfAt(src));
            case "PHONE":
                return _format(src, 3, src.length() - 4);
            case "BANKCARD":
                return _format(src, 4, src.length() - 4);
            case "ACCOUNT":
                return _format(src, src.length() - 4, src.length());
            default:
                return src;
        }
    }


    /**
     * 将字符串中从start..end之间的字符全部替换为*
     *
     * @param str   被格式化字符串
     * @param start 起始索引
     * @param end   结束索引
     * @return 格式化失败时返回原字符串
     */
    private static String _format(String str, int start, int end) throws Exception {
        if (_isBlank(str)) {
            throw new Exception("参数错误:str不能为空.");
        }
        if (start > str.length()) {
            throw new Exception("参数错误:start不能大于str的长度.");
        }
        if (start > end) {
            throw new Exception("参数错误:start不能大于end.");
        }
        char[] buf = str.toCharArray();
        while (start < end) {
            //空格不进行格式化
            if (String.valueOf(buf[start]).trim().isEmpty()) {
                start++;
                continue;
            }
            if (buf[start] == '-') {
                start++;
                continue;
            }
            buf[start++] = '*';
        }
        return String.valueOf(buf);
    }

    /**
     * 查找邮件地址中@的位置
     *
     * @param str
     * @return
     */
    private static int _indexOfAt(String str) throws Exception {
        if (!isEmail(str)) {
            throw new Exception("非法的EMAIL格式.");
        }
        //判断是合法email
        return str.indexOf('@');
    }

    private static final String ID15_REGEX = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    private static final String ID18_REGEX = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    private static final Pattern ID15_PATTERN = Pattern.compile(ID15_REGEX);
    private static final Pattern ID18_PATTERN = Pattern.compile(ID18_REGEX);

    /**
     * 是否为合法身份证号
     *
     * @param src
     * @return
     */
    private static boolean isID(String src) {
        return _isBlank(src) ? false : ID15_PATTERN.matcher(src).matches() || ID18_PATTERN.matcher(src).matches();
    }

    /**
     * 合法Email格式正则
     */
    private static final String EMAIL_REGEX = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * 判断是否为email
     *
     * @param src
     * @return
     */
    private static boolean isEmail(String src) {
        return _isBlank(src) ? false : EMAIL_PATTERN.matcher(src).matches();
    }

    /**
     * 合法手机格式正则
     */
    private static final String MOBILE_REGEX = "1([\\d]{10})";
    /**
     * 座机电话格式验证(区号+座机号码+分机号码)
     **/
    private static final String PHONE_REGEX = "^(0[0-9]{2,3}/-)?([2-9][0-9]{6,7})+(/-[0-9]{1,4})?$";

    private static Pattern MOBILE_PATTERN = Pattern.compile(MOBILE_REGEX);
    private static Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    /**
     * 判断是否为合法电话格式（手机/座机）
     *
     * @param src
     * @return
     */
    private static boolean isValidPhone(String src) {
        return _isBlank(src) ? false : MOBILE_PATTERN.matcher(src).matches() || PHONE_PATTERN.matcher(src).matches();
    }

    /**
     * 判断src不为空
     *
     * @param src
     * @return
     */
    private static boolean _isBlank(String src) {
        return src == null || src.trim().isEmpty();
    }
}