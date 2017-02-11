package cn.jinmiao.bbs.perfectstoryapp.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.adapter.MyCollectionAdapter;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.collection.PlayCollectionListActivity;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.history.HistoryPlayListActivity;
import cn.jinmiao.bbs.perfectstoryapp.main.play.PlayMusicListActivity;
import cn.jinmiao.bbs.perfectstoryapp.main.play.ShowContentActivity;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppConstant;
import cn.jinmiao.bbs.perfectstoryapp.utils.SharePreferenceObjectTools;

/**
 * Created by Administrator on 2016/12/27 0027.
 */

public class DownloadListActivity extends Activity {

    private ListView listView;

    private SharedPreferences sp ;

    private Map musics ;

    private List nameLists = new ArrayList();

    private ImageView mReturn;

    private List<MP3Info> lists;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);
        lists = new ArrayList<MP3Info>();

        listView = (ListView) findViewById(R.id.download_list);
        mReturn = (ImageView) findViewById(R.id.music_History_list_comeback_ImageView);
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sp = getSharedPreferences("music",MODE_PRIVATE);
        musics = sp.getAll();
        Iterator iterator = musics.keySet().iterator();
        while (iterator.hasNext()){
            String name = iterator.next().toString();
            nameLists.add(name);
            MP3Info info = new MP3Info();
            info.setMp3_path(name);
            info.setSubject(musics.get(name).toString());
            info.setId("1");
            lists.add(info);
        }



        listView.setAdapter(new DownloadMusicAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(DownloadListActivity.this,"点击了条目",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DownloadListActivity.this, ShowContentActivity.class);
                //添加一系列要传递的播放歌曲的数据
                // 必要的
                SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
                editor.putInt("currentPlayMusicId", Integer.valueOf(position));//当前歌曲位置
                editor.commit();

                intent.putExtra("albumName", "");//当前专辑名称
                //SharedPreferences pref = DownloadListActivity.this.getSharedPreferences("currentPlayAlbumDataFile",MODE_PRIVATE);
                //Integer nowPlayId = 0;
                String key = (String) nameLists.get(position);
                //intent.putExtra("url",key);
                intent.putExtra("listPosition", position + "");//当前歌曲在播放列表中的位置
                //intent.putExtra("musicname",musics.get(key).toString());
                SharePreferenceObjectTools.SharePreferencesSaveObject(DownloadListActivity.this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey", lists);




                intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_DOWNLOAD);//传递过去播放状态为重新开始播放状态
                startActivity(intent);
            }
        });
        System.out.print("");
    }


    private class DownloadMusicAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder ;
            if(convertView == null){
                holder = new Holder();
                convertView = View.inflate(parent.getContext(),R.layout.activity_download_item,null);
                holder.textView = (TextView) convertView.findViewById(R.id.musicname);
                convertView.setTag(holder);
            }else{
                holder = (Holder) convertView.getTag();
            }
            String key = (String) nameLists.get(position);
            holder.textView.setText(musics.get(key).toString());



            return convertView;
        }
    }


    private class Holder{
        public TextView textView;

    }

}
