package cn.jinmiao.bbs.perfectstoryapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.KidNovelStoryCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.bean.HomeCatalog;
import cn.jinmiao.bbs.perfectstoryapp.fragment.home.HomeFragment;

/**
 * Created by Administrator on 2016/12/15 0015.
 */

public class SecondMenuAdapter extends SimpleBaseAdapter<HomeCatalog> {


    private int mLocation ;

    private int[] icons0 = new int[]{
            R.mipmap.shuiqian0,
            R.mipmap.shuiqian1,
            R.mipmap.shuiqian2,
            R.mipmap.shuiqian3,
            R.mipmap.shuiqian4,
            R.mipmap.shuiqian5,
            R.mipmap.shuiqian6};

    private int[] icons1 = new int[]{
            R.mipmap.ertong0,
            R.mipmap.ertong1,
            R.mipmap.ertong2,
            R.mipmap.ertong3,
            R.mipmap.ertong4,
            R.mipmap.ertong5,
            R.mipmap.ertong6};
    private int[] icons2 = new int[]{
            R.mipmap.shige0,
            R.mipmap.shige1,
            R.mipmap.shige2,
            R.mipmap.shige3,
            R.mipmap.shige4,
            R.mipmap.shige5};
    private int[] icons3 = new int[]{
            R.mipmap.guoxue0,
            R.mipmap.guoxue1,
            R.mipmap.guoxue2,
            R.mipmap.guoxue3,
            R.mipmap.guoxue4,
            R.mipmap.guoxue5,
            R.mipmap.guoxue6};
    private int[] icons4 = new int[]{
            R.mipmap.kewen0,
            R.mipmap.kewen1,
            R.mipmap.kewen2,
            R.mipmap.kewen3,
            R.mipmap.kewen4,
            R.mipmap.kewen5,
            R.mipmap.kewen6};
    private int[] icons5 = new int[]{
            R.mipmap.diantai0,
            R.mipmap.diantai1,
            R.mipmap.diantai2,
            R.mipmap.diantai3,
            R.mipmap.diantai4,
            R.mipmap.diantai5};
    private int[] icons6 = new int[]{
            R.mipmap.tingli0,
            R.mipmap.tingli1,
            R.mipmap.tingli2,
            R.mipmap.tingli3,
            R.mipmap.tingli4};
    private int[] icons7 = new int[]{
            R.mipmap.yousheng0,
            R.mipmap.yousheng1,
            R.mipmap.yousheng2,
            R.mipmap.yousheng3,
            R.mipmap.yousheng4,
            R.mipmap.yousheng5,
            R.mipmap.yousheng6};



    /**
     * 实例化该对象，传递过来数据
     *
     * @param context
     * @param data
     */
    public SecondMenuAdapter(Context context, List<HomeCatalog> data, int mLocation) {
        super(context, data);
        this.mLocation = mLocation;
    }

    @Override
    public int getItemResource() {
        return R.layout.m_gradview_item;
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        ImageView iv = (ImageView) holder.getView( R.id.m_image);
        TextView tv = (TextView) holder.getView(R.id.m_text);

        switch (mLocation){
            case 0:
                Glide.with(convertView.getContext()).load(icons0[position]).into(iv);
               // iv.setImageResource(icons0[position]);
                break;
            case 1:
                Glide.with(convertView.getContext()).load(icons0[position]).into(iv);
                //iv.setImageResource(icons1[position]);
                break;
            case 2:
                Glide.with(convertView.getContext()).load(icons0[position]).into(iv);
               // iv.setImageResource(icons2[position]);
                break;
            case 3:
                Glide.with(convertView.getContext()).load(icons0[position]).into(iv);
                //iv.setImageResource(icons3[position]);
                break;
            case 4:
                Glide.with(convertView.getContext()).load(icons0[position]).into(iv);
               // iv.setImageResource(icons4[position]);
                break;
            case 5:
                Glide.with(convertView.getContext()).load(icons0[position]).into(iv);
               // iv.setImageResource(icons5[position]);
                break;
            case 6:
                Glide.with(convertView.getContext()).load(icons0[position]).into(iv);
                //iv.setImageResource(icons6[position]);
                break;
            case 7:
                Glide.with(convertView.getContext()).load(icons0[position]).into(iv);
               // iv.setImageResource(icons7[position]);
                break;


        }
        tv.setText(data.get(position).getTitle());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"当前位置"+position,Toast.LENGTH_SHORT).show();
                ((SecondMenuActivity)context).JumpAlbumActivityMethod(data.get(position).getId(),data.get(position).getTitle());


            }
        });
        return convertView;
    }
}
