package cn.jinmiao.bbs.perfectstoryapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;

/**
 * Created by zhaqingf on 2016/2/28.
 */
public class MyCollectionAdapter extends BaseAdapter {

    private Context mContext;
    private List<MP3Info> mp3InfoList;
    private MP3Info mp3Info;

    public MyCollectionAdapter(Context mContext, List<MP3Info> mp3InfoList) {
        this.mContext = mContext;
        this.mp3InfoList = mp3InfoList;
    }

    @Override
    public int getCount() {
        return mp3InfoList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        MyCollectionMusicListViewHolder myCollectionMusicListViewHolder;
        if(convertView==null){
            myCollectionMusicListViewHolder = new MyCollectionMusicListViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_music_play_listview, null);
            myCollectionMusicListViewHolder.music_List_num_TextView = (TextView) convertView.findViewById(R.id.PlayerListNumber);//播放列表序号
            myCollectionMusicListViewHolder.music_List_Title_Larger = (TextView) convertView.findViewById(R.id.music_list_title_larger);//播放歌曲名大标题
            myCollectionMusicListViewHolder.music_List_Title_Smaller = (TextView) convertView.findViewById(R.id.music_list_title_smaller);//播放歌曲名小标题
            myCollectionMusicListViewHolder.nowCollectionPlayImgView = (ImageView) convertView.findViewById(R.id.nowPlaySingListBtnImg_id);//正在播放图片
            convertView.setTag(myCollectionMusicListViewHolder);
        }else{
            myCollectionMusicListViewHolder = (MyCollectionMusicListViewHolder) convertView.getTag();
        }

        //获取当前选择的歌曲对象
        mp3Info = mp3InfoList.get(position);

        //播放歌曲序号
        myCollectionMusicListViewHolder.music_List_num_TextView.setText((position + 1) + "");
        //设置播放序号字体颜色为黑色
        myCollectionMusicListViewHolder.music_List_num_TextView.setTextColor(Color.BLACK);

        //当前播放列表每个选项赋值歌曲名称大标题
        myCollectionMusicListViewHolder.music_List_Title_Larger.setText(mp3Info.getSubject() + "");
        myCollectionMusicListViewHolder.music_List_Title_Larger.setTextColor(Color.BLACK);

        //当前播放专辑名称
        myCollectionMusicListViewHolder.music_List_Title_Smaller.setText(mp3Info.getSubject() + "");
        //当前播放专辑名称字体颜色
        myCollectionMusicListViewHolder.music_List_Title_Smaller.setTextColor(Color.GRAY);

        //修改播放图片不可见
        myCollectionMusicListViewHolder.nowCollectionPlayImgView.setVisibility(View.GONE);

        return convertView;
    }

    private final class MyCollectionMusicListViewHolder {
        TextView music_List_num_TextView;
        TextView music_List_Title_Larger;
        TextView music_List_Title_Smaller;
        ImageView nowCollectionPlayImgView;
    }
}
