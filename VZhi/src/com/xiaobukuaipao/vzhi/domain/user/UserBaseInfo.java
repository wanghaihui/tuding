package com.xiaobukuaipao.vzhi.domain.user;

import org.apache.http.cookie.CookieOrigin;

public class UserBaseInfo {
    private long userId;
    private String userName;
    // 确认码
    private String verifyCode;
    private String mobile;
    private String p;
    private String t;
    private String uid;
    
    private CookieOrigin Cookie;
    
    public CookieOrigin getCookie() {
		return Cookie;
	}

	public void setCookie(CookieOrigin cookie) {
		Cookie = cookie;
	}
	public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
