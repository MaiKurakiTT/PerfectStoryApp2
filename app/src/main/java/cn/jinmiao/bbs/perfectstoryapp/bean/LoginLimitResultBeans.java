package cn.jinmiao.bbs.perfectstoryapp.bean;

import java.io.Serializable;

/**
 * Created by zhaqingf on 2016/3/17.
 */
public class LoginLimitResultBeans implements Serializable{
    private String LoginLimit;

    public String getLoginLimit() {
        return LoginLimit;
    }

    public void setLoginLimit(String loginLimit) {
        LoginLimit = loginLimit;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
