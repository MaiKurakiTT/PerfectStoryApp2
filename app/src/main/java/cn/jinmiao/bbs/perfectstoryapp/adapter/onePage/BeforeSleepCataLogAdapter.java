package cn.jinmiao.bbs.perfectstoryapp.adapter.onePage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.HomeCatalog;
import cn.jinmiao.bbs.perfectstoryapp.fragment.home.HomeFragment;


/**
 * 睡前故事自定义适配器
 * Created by fairy on 2016/1/10.
 */
public class BeforeSleepCataLogAdapter extends BaseAdapter {

    private HomeFragment homeFragment;
    private Context mContext;
    private List<HomeCatalog> homeCatalogList;
    private HomeCatalog homeCatalog;

    public BeforeSleepCataLogAdapter(HomeFragment homeFragment, Context mContext, List<HomeCatalog> homeCatalogList) {
        this.homeFragment = homeFragment;
        this.mContext = mContext;
        this.homeCatalogList = homeCatalogList;
    }

    @Override
    public int getCount() {
        return homeCatalogList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
         BeforeSleepViewHolder beforeSleepViewHolder=null;
        if(convertView==null)
        {
            beforeSleepViewHolder=new BeforeSleepViewHolder();
            convertView=LayoutInflater.from(mContext).inflate(R.layout.item_fragment_home_before_sleep,null);
            beforeSleepViewHolder.beforeSleepTitle_TextView=(TextView) convertView.findViewById(R.id.beforeSleepStory_Item_id);
            convertView.setTag(beforeSleepViewHolder);
        }else{
            beforeSleepViewHolder=(BeforeSleepViewHolder)convertView.getTag();
        }

        homeCatalog=homeCatalogList.get(position);
        beforeSleepViewHolder.beforeSleepTitle_TextView.setText(homeCatalog.getTitle()+"");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeCatalog=homeCatalogList.get(position);
                homeFragment.JumpAlbumActivityMethod(homeCatalog.getId(),homeCatalog.getTitle()+"");
            }
        });


        
        return convertView;
    }

    final  class BeforeSleepViewHolder{
        TextView beforeSleepTitle_TextView;
    }
}
