package cn.jinmiao.bbs.perfectstoryapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.AlbumCatelog;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;
import cn.jinmiao.bbs.perfectstoryapp.bean.SearchResultBean;
import cn.jinmiao.bbs.perfectstoryapp.main.search.SearchPlayListActivity;

/**
 * Created by zhaqingf on 2016/2/29.
 */
public class SearchResultAdapter extends BaseAdapter {
    private Context mContext;
    private List<SearchResultBean> albumCatelogList;
    private SearchResultBean albumCatelog;
    private SearchPlayListActivity searchPlayListActivity;

    public SearchResultAdapter(SearchPlayListActivity searchPlayListActivity,Context mContext, List<SearchResultBean> albumCatelogList) {
        this.searchPlayListActivity=searchPlayListActivity;
        this.mContext = mContext;
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
        final SearchAlbumViewHolder albumViewHolder;
        if(convertView==null)
        {
            albumViewHolder=new SearchAlbumViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_search_album_catelog_layout,null);
            albumViewHolder.nameTitileTextView=(TextView)convertView.findViewById(R.id.Search_album_name_TextView);
            albumViewHolder.shuyuzhuanjiTextView=(TextView)convertView.findViewById(R.id.Search_zhuanji_TextView);
            albumViewHolder.zhuozheTextView=(TextView)convertView.findViewById(R.id.Search_zhuozhe_TextView);
            albumViewHolder.zhuboTextView=(TextView)convertView.findViewById(R.id.Search_zhubo_TextView);
            albumViewHolder.imageView=(ImageView)convertView.findViewById(R.id.search_albumImageView_id);
            convertView.setTag(albumViewHolder);
        }else{
            albumViewHolder=(SearchAlbumViewHolder)convertView.getTag();
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        albumCatelog= albumCatelogList.get(position);

        //添加专辑图片
        Glide.with(mContext) .load("http://www.zc532.com/uppic/"+albumCatelog.getPic()) .into(albumViewHolder.imageView);
        albumViewHolder.nameTitileTextView.setText(albumCatelog.getSubject());
        albumViewHolder.shuyuzhuanjiTextView.setText("所属专辑:" + albumCatelog.getSubject1());
        albumViewHolder.zhuozheTextView.setText("作者:好好听故事网");
        albumViewHolder.zhuboTextView.setText("主播:"+albumCatelog.getZhubo());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumCatelog = albumCatelogList.get(position);
                searchPlayListActivity.JumpPlayListActivityMethod(albumCatelog.getId1(),albumCatelog.getSubject1()+"");
            }
        });

        return convertView;
    }

    final class SearchAlbumViewHolder{
        TextView nameTitileTextView;
        TextView shuyuzhuanjiTextView;
        TextView zhuozheTextView;
        TextView zhuboTextView;
        ImageView imageView;
    }
}
