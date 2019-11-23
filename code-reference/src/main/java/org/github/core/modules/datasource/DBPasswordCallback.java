package org.github.core.modules.datasource;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.util.DruidPasswordCallback;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.Properties;

/**
 * Created by java on 2017/6/23.
 */
public class DBPasswordCallback extends DruidPasswordCallback {
    private static Logger logger = LoggerFactory.getLogger(DBPasswordCallback.class);
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String pwd = properties.getProperty("password");
        if (StringUtils.isNotBlank(pwd)) {
            if (pwd.length() < 20) {
               // setPassword(pwd.toCharArray());
             //   return;
            }
            try {
                //这里的password是将jdbc.properties配置得到的密码进行解密之后的值
                //所以这里的代码是将密码进行解密
                PublicKey publicKey = ConfigTools.getPublicKey(null);
                String passwordPlainText = ConfigTools.decrypt(publicKey, pwd);
                setPassword(passwordPlainText.toCharArray());
            } catch (Exception e) {
                logger.error("未做加密处理");
                setPassword(pwd.toCharArray());
            }
        }
    }
}
