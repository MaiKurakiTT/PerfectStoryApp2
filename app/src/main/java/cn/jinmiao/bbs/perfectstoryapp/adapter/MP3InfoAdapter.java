package cn.jinmiao.bbs.perfectstoryapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;
import cn.jinmiao.bbs.perfectstoryapp.main.play.PlayMusicListActivity;

/**
 * Created by fairy on 2016/1/19.
 */
public class MP3InfoAdapter extends BaseAdapter {

    private Context mContext;//上下文对象
    private List<MP3Info> mp3InfoList;//播放歌曲列表集合
    private MP3Info mp3Info;//当前歌曲对象
    private Integer NowPlayingMusicId;//当前播放歌曲Id
    private Integer oldAlbumId;//正在播放的专辑Id
    private Integer currentAlbumId;//重新选择的当前专辑Id
    private String currentAlbumName;//当前专辑名称
    private PlayMusicListActivity playMusicListActivity;

    //有参数构造方法
    public MP3InfoAdapter(Context mContext, List<MP3Info> mp3InfoList,String mAlbumName,Integer mAlbumId,PlayMusicListActivity playMusicListActivity) {
        this.mContext = mContext;
        this.mp3InfoList = mp3InfoList;
        this.currentAlbumName=mAlbumName;
        this.currentAlbumId=mAlbumId;
        this.playMusicListActivity=playMusicListActivity;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyMusicListViewHolder myMusicListViewHolder;
        if (convertView == null) {
            myMusicListViewHolder = new MyMusicListViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_music_play_listview, null);
            myMusicListViewHolder.music_List_num_TextView = (TextView) convertView.findViewById(R.id.PlayerListNumber);//播放列表序号
            myMusicListViewHolder.music_List_Title_Larger = (TextView) convertView.findViewById(R.id.music_list_title_larger);//播放歌曲名大标题
            myMusicListViewHolder.music_List_Title_Smaller = (TextView) convertView.findViewById(R.id.music_list_title_smaller);//播放歌曲名小标题
            myMusicListViewHolder.nowPlayImgView = (ImageView) convertView.findViewById(R.id.nowPlaySingListBtnImg_id);//正在播放图片
            convertView.setTag(myMusicListViewHolder);
        } else {
            myMusicListViewHolder = (MyMusicListViewHolder) convertView.getTag();
        }

        //获取当前选择的歌曲对象
        mp3Info = mp3InfoList.get(position);

        //播放歌曲序号
        myMusicListViewHolder.music_List_num_TextView.setText((position + 1) + "");
        //设置播放序号字体颜色为黑色
        myMusicListViewHolder.music_List_num_TextView.setTextColor(Color.BLACK);

        //当前播放列表每个选项赋值歌曲名称大标题
        myMusicListViewHolder.music_List_Title_Larger.setText(mp3Info.getSubject() + "");
        myMusicListViewHolder.music_List_Title_Larger.setTextColor(Color.BLACK);

        //当前播放专辑名称
        myMusicListViewHolder.music_List_Title_Smaller.setText(currentAlbumName + "");
        //当前播放专辑名称字体颜色
        myMusicListViewHolder.music_List_Title_Smaller.setTextColor(Color.GRAY);

        //读取数据库获取当前正在播放歌曲位置
        NowPlayingMusicId=getCurrentPlayPosition();/// 文件读出来的
        oldAlbumId=getNowPlayAlbumId();//重新选择的结果
        //修改播放图片不可见
        myMusicListViewHolder.nowPlayImgView.setVisibility(View.GONE);

        if(oldAlbumId.equals(currentAlbumId)){
            if(position==NowPlayingMusicId){

                //修改播放图片为可见
                myMusicListViewHolder.nowPlayImgView.setVisibility(View.VISIBLE);

            }else{
                //修改播放图片不可见
                myMusicListViewHolder.nowPlayImgView.setVisibility(View.GONE);
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"精彩故事正在准备中,请稍候片刻...",Toast.LENGTH_LONG).show();
                //跳转到播放界面   传递当前播放歌曲Id,当前播放列表,当前专辑名称
                playMusicListActivity.playMusic(position,mp3InfoList,currentAlbumName,currentAlbumId);
            }
        });

        return convertView;
    }

    private final class MyMusicListViewHolder {
        TextView music_List_num_TextView;
        TextView music_List_Title_Larger;
        TextView music_List_Title_Smaller;
        ImageView nowPlayImgView;
    }

    private Integer getCurrentPlayPosition(){
            SharedPreferences pref = mContext.getSharedPreferences("currentPlayAlbumDataFile", mContext.MODE_PRIVATE);
        Integer NowPlayingMusicId = pref.getInt("currentPlayMusicId",-1);
        return NowPlayingMusicId;
    }

    private Integer getNowPlayAlbumId(){
        SharedPreferences pref = mContext.getSharedPreferences("currentPlayAlbumDataFile", mContext.MODE_PRIVATE);
        Integer oldAlbumId = pref.getInt("currentPlayListAlbumId", -1);
        return oldAlbumId;
    }

}
