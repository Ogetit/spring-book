package com.github.core.auth;

import java.util.Date;

import com.github.core.utils.SystemUtil;

public class AppUserDetail {
    private String uid;
    private String username;
    private String password;
    private String name;
    private Date createTime;
    private String verifyCode;

    public AppUserDetail() {}

    public AppUserDetail(String username, String password) {
        this.username = username;
        this.setMD5Password(password);
        this.createTime = new Date();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMD5Password(String password) {
        this.password = SystemUtil.md5(password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
