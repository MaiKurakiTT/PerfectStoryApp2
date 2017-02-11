package cn.jinmiao.bbs.perfectstoryapp.bean;

import java.io.Serializable;

/**
 * Created by zhaqingf on 2016/2/28.
 */
public class CancelCollectionBean implements Serializable {


    /**
     * CancleCollection : error
     */

    private String CancleCollection;

    public void setCancleCollection(String CancleCollection) {
        this.CancleCollection = CancleCollection;
    }

    public String getCancleCollection() {
        return CancleCollection;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
