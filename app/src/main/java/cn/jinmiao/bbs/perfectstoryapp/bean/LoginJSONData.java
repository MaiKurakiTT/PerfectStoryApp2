package cn.jinmiao.bbs.perfectstoryapp.bean;

import java.io.Serializable;

/**
 * Created by zhaqingf on 2016/1/28.
 */
public class LoginJSONData implements Serializable{


    /**
     * GetLogin : success
     * uid : 19
     */

    private String GetLogin;
    private String uid;

    @Override
    public String toString() {
        return super.toString();
    }

    public void setGetLogin(String GetLogin) {
        this.GetLogin = GetLogin;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGetLogin() {
        return GetLogin;
    }

    public String getUid() {
        return uid;
    }

}
