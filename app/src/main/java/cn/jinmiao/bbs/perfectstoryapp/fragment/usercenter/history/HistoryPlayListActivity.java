package cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.history;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.adapter.MyCollectionAdapter;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;
import cn.jinmiao.bbs.perfectstoryapp.db.HistoryPlayDataBase;
import cn.jinmiao.bbs.perfectstoryapp.main.play.ShowContentActivity;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppConstant;
import cn.jinmiao.bbs.perfectstoryapp.utils.SharePreferenceObjectTools;

public class HistoryPlayListActivity extends AppCompatActivity {

    private ListView music_History_ListView;
    private List<MP3Info> mp3InfoList=new ArrayList<MP3Info>();
    private ImageView music_History_list_comeback_ImageView;
    private MyCollectionAdapter myHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCmpaActivityCollector.addAppCompatActivity(this);
        setContentView(R.layout.activity_history_play_list);

        music_History_list_comeback_ImageView=(ImageView)findViewById(R.id.music_History_list_comeback_ImageView);
        music_History_list_comeback_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryPlayListActivity.this.finish();
            }
        });
        music_History_ListView=(ListView)findViewById(R.id.music_History_ListView_id);

       mp3InfoList=findAllHistoryMusicList(HistoryPlayListActivity.this);

        myHistoryAdapter=new MyCollectionAdapter(HistoryPlayListActivity.this,mp3InfoList);
        music_History_ListView.setAdapter(myHistoryAdapter);

        music_History_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayMusicListByPosition(position);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCmpaActivityCollector.addAppCompatActivity(this);
    }

    private void PlayMusicListByPosition(int position) {
        if(mp3InfoList!=null){
            //每次单击播放列表就尝试清理当前播放列表,避免歌曲列表混乱
            try {
                ClearData();
            } catch (Exception e) {
               // Log.v("FairyDebug", "PlayMusicListActivity----------------------不需要清理当前播放列表");
            }

            //持久化存储专辑Id 专辑名称 播放位置
            //更新当前播放队列信息  持久化存储方便首界面快速进入
            SharePreferenceObjectTools.SharePreferencesSaveObject(HistoryPlayListActivity.this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey", mp3InfoList);
            //更新当前播放位置
            SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
            editor.putInt("currentPlayMusicId", Integer.valueOf(position));//当前歌曲位置
            editor.commit();

            //将当前播放音乐对象信息传递到下一个界面
            // 定义Intent对象，跳转到播放界面
            Intent intent = new Intent(HistoryPlayListActivity.this, ShowContentActivity.class);
            //添加一系列要传递的播放歌曲的数据
            // 必要的
            intent.putExtra("listPosition",position + "");//当前歌曲在播放列表中的位置
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);//传递过去播放状态为重新开始播放状态
            startActivity(intent);
        }

    }

    private void ClearData() {

        File file = new File("/data/data/"
                + HistoryPlayListActivity.this.getPackageName().toString() + "/shared_prefs",
                "PlayMusicListSharePreferenceFile.xml");

        if (file.exists()) {

            SharedPreferences sharedata = HistoryPlayListActivity.this.getApplication().getSharedPreferences("PlayMusicListSharePreferenceFile", Context.MODE_PRIVATE);
            sharedata.edit().clear().commit();
            file.delete();
        }
    }

    private List<MP3Info> findAllHistoryMusicList(Context mContext){

        //打开数据库
        HistoryPlayDataBase historyMusicDBHelper=new HistoryPlayDataBase(mContext,"HistoryMusic.db",null,1);
        SQLiteDatabase db = historyMusicDBHelper.getReadableDatabase();

        Cursor cursor = db.query("t_history_music", null, null, null, null, null, null);

        //开始查询
        if(cursor.moveToFirst()){
            do {
                MP3Info historyMP3=new MP3Info();
                historyMP3.setId(cursor.getString(cursor.getColumnIndex("history_music_id")));
                historyMP3.setSubject(cursor.getString(cursor.getColumnIndex("history_music_subject")));
                historyMP3.setMp3_path(cursor.getString(cursor.getColumnIndex("history_music_path")));
                mp3InfoList.add(historyMP3);
            } while (cursor.moveToNext());
        }
        return mp3InfoList;
    }
}
