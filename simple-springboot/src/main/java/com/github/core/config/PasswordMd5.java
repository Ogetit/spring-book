package com.github.core.config;

import com.github.core.utils.SystemUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密md5工具
 * @author 欧阳洁
 */
public class PasswordMd5 implements PasswordEncoder {


    @Override
    public String encode(CharSequence rawPassword) {
        final int md5Len = 32;
        String password = rawPassword.toString();
        if (password.length() == md5Len) {
            return password;
        }
        return SystemUtil.md5(password);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String password = encode(rawPassword);
        return password.equalsIgnoreCase(encodedPassword);
    }
}
