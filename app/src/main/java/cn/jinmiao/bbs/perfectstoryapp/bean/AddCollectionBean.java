package cn.jinmiao.bbs.perfectstoryapp.bean;

import java.io.Serializable;

/**
 * Created by zhaqingf on 2016/2/28.
 */
public class AddCollectionBean implements Serializable{


    /**
     * AddCollection : success
     */

    private String AddCollection;

    public void setAddCollection(String AddCollection) {
        this.AddCollection = AddCollection;
    }

    public String getAddCollection() {
        return AddCollection;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
