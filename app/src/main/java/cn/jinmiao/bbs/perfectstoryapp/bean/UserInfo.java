package cn.jinmiao.bbs.perfectstoryapp.bean;

import java.io.Serializable;

/**
 * Created by zhaqingf on 2016/1/30.
 */
public class UserInfo implements Serializable{


    /**
     * username : 金苗论坛帅
     * salt : a564b2
     * exptime : 1478447999
     * jointime : 1447830731
     * isyear : False
     * vgid : 1
     * growth : 378
     * posts : 2401
     * threads : 1521
     * credits : 1754
     */

    private String username;
    private String salt;
    private String exptime;
    private String jointime;
    private String isyear;
    private String vgid;
    private String growth;
    private String posts;
    private String threads;
    private String credits;

    @Override
    public String toString() {
        return super.toString();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setExptime(String exptime) {
        this.exptime = exptime;
    }

    public void setJointime(String jointime) {
        this.jointime = jointime;
    }

    public void setIsyear(String isyear) {
        this.isyear = isyear;
    }

    public void setVgid(String vgid) {
        this.vgid = vgid;
    }

    public void setGrowth(String growth) {
        this.growth = growth;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public void setThreads(String threads) {
        this.threads = threads;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getUsername() {
        return username;
    }

    public String getSalt() {
        return salt;
    }

    public String getExptime() {
        return exptime;
    }

    public String getJointime() {
        return jointime;
    }

    public String getIsyear() {
        return isyear;
    }

    public String getVgid() {
        return vgid;
    }

    public String getGrowth() {
        return growth;
    }

    public String getPosts() {
        return posts;
    }

    public String getThreads() {
        return threads;
    }

    public String getCredits() {
        return credits;
    }
}
