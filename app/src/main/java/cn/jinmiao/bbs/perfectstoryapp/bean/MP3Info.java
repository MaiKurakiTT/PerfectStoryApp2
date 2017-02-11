package cn.jinmiao.bbs.perfectstoryapp.bean;

import java.io.Serializable;

/**
 * Created by fairy on 2016/1/19.
 * 音乐实体类
 */
public class MP3Info implements Serializable{

    /**
     * id : 466
     * subject : 01 斑马身上的花纹有什么用
     * mp3_file : feixue0zv.mp3
     * sid : 245
     * time_len : 3
     * score : 6
     * listen_time : 1106
     * mp3_path : 03zhishi/01/feixue0zv.mp3
     * jm3_path : 03zhishi/01//feixue0zv.jm3
     */

    private String id;
    private String subject;
    private String mp3_file;
    private String sid;
    private String time_len;
    private String score;
    private String listen_time;
    private String mp3_path;
    private String jm3_path;

    public void setId(String id) {
        this.id = id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMp3_file(String mp3_file) {
        this.mp3_file = mp3_file;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setTime_len(String time_len) {
        this.time_len = time_len;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setListen_time(String listen_time) {
        this.listen_time = listen_time;
    }

    public void setMp3_path(String mp3_path) {
        this.mp3_path = mp3_path;
    }

    public void setJm3_path(String jm3_path) {
        this.jm3_path = jm3_path;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getMp3_file() {
        return mp3_file;
    }

    public String getSid() {
        return sid;
    }

    public String getTime_len() {
        return time_len;
    }

    public String getScore() {
        return score;
    }

    public String getListen_time() {
        return listen_time;
    }

    public String getMp3_path() {
        return mp3_path;
    }

    public String getJm3_path() {
        return jm3_path;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
