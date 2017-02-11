package cn.jinmiao.bbs.perfectstoryapp.bean;

import java.io.Serializable;

/**
 * Created by zhaqingf on 2016/2/28.
 */
public class DescrptCollection  implements Serializable {

    /**
     * IsCollection : no
     */

    private String IsCollection;

    public void setIsCollection(String IsCollection) {
        this.IsCollection = IsCollection;
    }

    public String getIsCollection() {
        return IsCollection;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
