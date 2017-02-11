package cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.collection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.adapter.MyCollectionAdapter;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;
import cn.jinmiao.bbs.perfectstoryapp.main.play.ShowContentActivity;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppConstant;
import cn.jinmiao.bbs.perfectstoryapp.utils.SharePreferenceObjectTools;

public class PlayCollectionListActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView comeBackImageView;
    private ListView music_Collection_ListView;
    private String music_Collection_JSONData;
    private MyCollectionAdapter myCollectionAdapter;
    private List<MP3Info> mp3InfoList=new ArrayList<MP3Info>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCmpaActivityCollector.addAppCompatActivity(this);
        setContentView(R.layout.activity_play_collection_list);

        findViewById();

        music_Collection_JSONData=getIntent().getStringExtra("myCollectJSONList");

        try {
            Gson myCollectionGson=new Gson();
            mp3InfoList=myCollectionGson.fromJson(music_Collection_JSONData,new TypeToken<List<MP3Info>>() {}.getType());
        } catch (JsonSyntaxException e) {
            Toast.makeText(PlayCollectionListActivity.this,"解析异常-1",Toast.LENGTH_SHORT).show();
        }

        myCollectionAdapter=new MyCollectionAdapter(PlayCollectionListActivity.this,mp3InfoList);
        music_Collection_ListView.setAdapter(myCollectionAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCmpaActivityCollector.removeAppCompatActivity(this);
    }

    private void findViewById() {
        comeBackImageView=(ImageView)findViewById(R.id.music_Collection_list_comeback_ImageView);
        comeBackImageView.setOnClickListener(this);
        music_Collection_ListView=(ListView)findViewById(R.id.music_Collection_ListView_id);
        music_Collection_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PlayMusicListByPosition(position);

            }
        });


    }

    private void PlayMusicListByPosition(int position) {
        if(mp3InfoList!=null){
            //每次单击播放列表就尝试清理当前播放列表,避免歌曲列表混乱
            try {
                ClearData();
            } catch (Exception e) {
                //Log.v("FairyDebug", "PlayMusicListActivity----------------------不需要清理当前播放列表");
            }

            //持久化存储专辑Id 专辑名称 播放位置
            //更新当前播放队列信息  持久化存储方便首界面快速进入
            SharePreferenceObjectTools.SharePreferencesSaveObject(PlayCollectionListActivity.this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey", mp3InfoList);
            //更新当前播放位置
            SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
            editor.putInt("currentPlayMusicId", Integer.valueOf(position));//当前歌曲位置
            editor.commit();

            //将当前播放音乐对象信息传递到下一个界面
            // 定义Intent对象，跳转到播放界面
            Intent intent = new Intent(PlayCollectionListActivity.this, ShowContentActivity.class);
            //添加一系列要传递的播放歌曲的数据
            // 必要的
            intent.putExtra("listPosition",position + "");//当前歌曲在播放列表中的位置
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);//传递过去播放状态为重新开始播放状态
            startActivity(intent);
        }

    }

    private void ClearData() {

        File file = new File("/data/data/"
                + PlayCollectionListActivity.this.getPackageName().toString() + "/shared_prefs",
                "PlayMusicListSharePreferenceFile.xml");

        if (file.exists()) {

            SharedPreferences sharedata = PlayCollectionListActivity.this.getApplication().getSharedPreferences("PlayMusicListSharePreferenceFile", Context.MODE_PRIVATE);
            sharedata.edit().clear().commit();
            file.delete();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.music_Collection_list_comeback_ImageView:
                PlayCollectionListActivity.this.finish();
                break;
            default:
                break;
        }

    }
}
