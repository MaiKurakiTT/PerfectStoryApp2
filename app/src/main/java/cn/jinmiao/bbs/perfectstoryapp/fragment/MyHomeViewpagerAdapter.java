package cn.jinmiao.bbs.perfectstoryapp.fragment;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class MyHomeViewpagerAdapter extends PagerAdapter {

    private int[] icons;

    private Context mContext;

    private List<View> images = new ArrayList<View>();



    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(images.get(position));
        return images.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(images.get(position));
    }

    public int[] getIcons() {
        return icons;
    }

    public void setIcons(int[] icons) {
        this.icons = icons;
    }

    public MyHomeViewpagerAdapter(Context mContext,List<View> views) {
        this.mContext = mContext;
        this.images = views;
    }
}
