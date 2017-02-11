package cn.jinmiao.bbs.perfectstoryapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.AlbumCatelog;
import cn.jinmiao.bbs.perfectstoryapp.fragment.home.album.AlbumCateLogActivity;

/**
 * 专辑列表自定义适配器
 * Created by fairy on 2016/1/24.
 */
public class AlBumAdapter extends BaseAdapter {
    private Context context;
    private List<AlbumCatelog>  albumCatelogList;
    private AlbumCatelog albumCatelog;
    private AlbumCateLogActivity albumCateLogActivity;


    public AlBumAdapter(AlbumCateLogActivity albumCateLogActivity ,Context context, List<AlbumCatelog> albumCatelogList) {
        this.albumCateLogActivity=albumCateLogActivity;
        this.context = context;
        this.albumCatelogList = albumCatelogList;
    }

    @Override
    public int getCount() {
        return albumCatelogList.size();
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
        final AlbumViewHolder albumViewHolder;
        if(convertView==null)
        {
            albumViewHolder=new AlbumViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_album_catelog_layout,null);
            albumViewHolder.nameTitileTextView=(TextView)convertView.findViewById(R.id.album_name_TextView);
            albumViewHolder.time_and_CountTextView=(TextView)convertView.findViewById(R.id.time_and_counts_TextView);
            albumViewHolder.imageView=(ImageView)convertView.findViewById(R.id.albumImageView_id);
            albumViewHolder.address = (TextView) convertView.findViewById(R.id.address);
            albumViewHolder.count = (TextView) convertView.findViewById(R.id.count);
            albumViewHolder.first = (TextView) convertView.findViewById(R.id.fitst);
            convertView.setTag(albumViewHolder);
        }else{
            albumViewHolder=(AlbumViewHolder)convertView.getTag();
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        albumCatelog= albumCatelogList.get(position);

        //添加专辑图片
        Glide.with(context) .load("http://www.zc532.com/uppic/"+albumCatelog.getPic()) .into(albumViewHolder.imageView);


        albumViewHolder.time_and_CountTextView.setText("播音 : " + albumCatelog.getZhubo() );
        albumViewHolder.count.setText("时长 : "  + albumCatelog.getTime_len() + "分钟" ); //
        String[] s = albumCatelog.getSubject().toString().split(" ");
        if(s.length>1){
            albumViewHolder.nameTitileTextView.setText("标题 : "+s[0]);
            albumViewHolder.first.setText("首篇 : " + s[1]);
        }else{
            albumViewHolder.nameTitileTextView.setText("标题 : "+s[0]);
            albumViewHolder.first.setText("首篇 : " + "好好听故事");
        }


        albumViewHolder.address.setText("学段 : " + albumCatelog.getAge() + "岁");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumCatelog= albumCatelogList.get(position);
                albumCateLogActivity.JumpPlayListActivityMethod(albumCatelog.getId(),albumCatelog.getSubject()+"");
            }
        });


        return convertView;
    }

    final class AlbumViewHolder{
        TextView nameTitileTextView;
        TextView time_and_CountTextView;
        TextView address;
        TextView first;
        TextView count;
        ImageView imageView;
    }


}
