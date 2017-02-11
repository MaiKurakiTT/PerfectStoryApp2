package cn.jinmiao.bbs.perfectstoryapp.bean;

import java.io.Serializable;

/**
 * Created by zhaqingf on 2016/3/16.
 */
public class ModifyResultBean implements Serializable{


    /**
     * ChangePassword : OldPsError
     */

    private String ChangePassword;

    public String getChangePassword() {
        return ChangePassword;
    }

    public void setChangePassword(String ChangePassword) {
        this.ChangePassword = ChangePassword;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
