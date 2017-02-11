package cn.jinmiao.bbs.perfectstoryapp.bean;

import java.io.Serializable;

/**
 * Created by fairy on 2016/1/10.
 */
public class HomeCatalog implements Serializable {

    /**
     * id : 57
     * title : 童话故事
     */

    private String id;
    private String title;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
