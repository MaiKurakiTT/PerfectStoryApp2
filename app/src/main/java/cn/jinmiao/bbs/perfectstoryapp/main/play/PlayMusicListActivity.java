package cn.jinmiao.bbs.perfectstoryapp.main.play;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.adapter.MP3InfoAdapter;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppConstant;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyGetHttpUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyHttpCallbackListenerUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.SharePreferenceObjectTools;


public class PlayMusicListActivity extends AppCompatActivity implements OnClickListener {

    private ListView musicListView;//播放列表
    private ImageView music_List_ComeBack_ImageView;//音乐播放列表返回按钮

    private List<MP3Info> mp3InfoList = new ArrayList<MP3Info>();//播放列表
    private MP3InfoAdapter mp3InfoAdapter;// 播放列表自定义适配器

    private String musicListJsonData;// 获取网络JSON数据
    private String musicListURL;// 获取网络数据URL接口

    private Integer currentPlayListAlbumId;//专辑Id
    private String currentAlbumName;//专辑名称

    private ImageView jumpPlayContentPageBtn;

    private TextView title_TextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Log.v("FairyDebug", "PlayMusicListActivity --------OnCreate() ");
        AppCmpaActivityCollector.addAppCompatActivity(this);
        setContentView(R.layout.activity_play_music_list);

        //找ID获取控件
        findViewById();


    }

    /**
     * onStart()     活动由不可见变为可见的时候调用 重新适配数据
     **/
    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * 重新写返回键方法
     ***/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 键盘码如果是返回键 则返回上一页面
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            PlayMusicListActivity.this.finish();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新UI获取网络数据并进行数据适配
        GetWebData();
    }

    // 获取网络数据
    private void GetWebData() {


        //获取上一个界面传递的数据
        // PlayMusicListActivity  <-------------AlbumCateLogActivity
        //播放列表Id
       currentPlayListAlbumId =Integer.valueOf(this.getIntent().getStringExtra("mAlbumId"));//获取列表Id
       currentAlbumName=this.getIntent().getStringExtra("mAlbumName");//专辑名称
        // 测试
//        Log.v("FairyDebug", "获取上一个界面播放列表所需要的专辑Id=" + currentPlayListAlbumId);
//        Log.v("FairyDebug", "获取上一个界面播放列表所需要要的专辑名称=" +currentAlbumName);

        //处理播放列表异常
        if (currentPlayListAlbumId ==null|| currentAlbumName==null ) {
            Toast.makeText(PlayMusicListActivity.this, "获取播放列表失败,请返回重试!", Toast.LENGTH_SHORT).show();
            return;
        }

        //修改标题
        title_TextView.setText(currentAlbumName+"");

        //构造URL获取播放列表
        musicListURL = this.getString(R.string.getMusicListURL) + "?action=getMusic&getMusic=" + currentPlayListAlbumId;
        //测试
        //Log.v("FairyDebug", "获取音乐列表接口URL:" + musicListURL);

        //发送Get请求获取数据
        MyGetHttpUtil.mySendHttpRequestUtils(musicListURL, new MyHttpCallbackListenerUtil() {

            //处理服务器返回成功时的方法
            @Override
            public void onFinish(String response) {
                musicListJsonData = response;// Response 服务器返回的 Json 数据
                //测试
              //  Log.v("FairyDebug", "获取音乐列表JSON数据：" + musicListJsonData);
                getMusicListHandle.sendEmptyMessage(1);//服务器成功得到响应处理交给Handle 更新UI
            }

            // 处理服务器返回失败时的方法
            @Override
            public void onError(Exception e) {
                getMusicListHandle.sendEmptyMessage(2);//服务响应失败时处理交给Handle 更新UI
            }
        });

    }

    private Handler getMusicListHandle = new Handler() {
        //处理服务器返回成功和失败的方法
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://服务器返回成功处理
                    if(!musicListJsonData.trim().equals("[]"))
                    {
                        //使用GSON解析JSON数据
                        try {
                            Gson musicListGson = new Gson();
                            mp3InfoList = musicListGson.fromJson(musicListJsonData, new TypeToken<List<MP3Info>>() {}.getType());
                            //使用自定义适配器适配数据 传递过去当前上下文对象和播放列表对象 专辑名称  专辑Id
                            mp3InfoAdapter = new MP3InfoAdapter(PlayMusicListActivity.this.getApplicationContext(), mp3InfoList,currentAlbumName,currentPlayListAlbumId,PlayMusicListActivity.this);
                            musicListView.setAdapter(mp3InfoAdapter);
                        } catch (JsonSyntaxException e) {
                            Toast.makeText(PlayMusicListActivity.this, "播放列表数据解析异常,请稍后重试!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(PlayMusicListActivity.this, "该专辑下没有找到相关歌曲", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(PlayMusicListActivity.this, "服务器连接失败,请联网再试试", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void findViewById() {

        title_TextView=(TextView)findViewById(R.id.bar_top_title);

        //播放列表返回按钮
        music_List_ComeBack_ImageView = (ImageView) findViewById(R.id.music_list_comeback_ImageView);
        //播放列表返回按钮设置监听
        music_List_ComeBack_ImageView.setOnClickListener(this);

        //播放列表
        musicListView = (ListView) findViewById(R.id.music_ListView_id);

        //跳转到当前正在播放列表
        jumpPlayContentPageBtn=(ImageView)findViewById(R.id.jumpPlayContentPageImg_id);
        jumpPlayContentPageBtn.setOnClickListener(this);

    }





    // 当前选中播放歌曲单击监听事件  传递当前选中歌曲位置,当前播放列表,当前专辑名称
    public void playMusic(int listPosition, List<MP3Info> mp3InfoList,String mAlbumName,Integer mAlbumId) {

//             Log.v("FairyDebug", "PlayMusicListActivity----------------------跳转到播放界面显示当前播放队列长度为" + mp3InfoList.size());
//             Log.v("FairyDebug", "PlayMusicListActivity----------------------跳转到播放界面显示当前专辑名称" +mAlbumName);
//             Log.v("FairyDebug", "PlayMusicListActivity----------------------跳转到播放界面显示当前歌曲播放位置" +listPosition);
            //如果当前播放列表不为空
            if (mp3InfoList != null) {

                //每次单击播放列表就尝试清理当前播放列表,避免歌曲列表混乱
                try {
                    ClearData();
                } catch (Exception e) {
//                    Log.v("FairyDebug", "PlayMusicListActivity----------------------不需要清理当前播放列表");
                }

                //持久化存储专辑Id 专辑名称 播放位置
                //更新当前播放队列信息  持久化存储方便首界面快速进入
                SharePreferenceObjectTools.SharePreferencesSaveObject(PlayMusicListActivity.this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey", mp3InfoList);
                //更新当前播放位置
                SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
                editor.putInt("currentPlayListAlbumId",mAlbumId);//当前专辑Id 持久化
                editor.putString("currentAlbumName",mAlbumName);//当前播放专辑名称
                editor.putInt("currentPlayMusicId", Integer.valueOf(listPosition));//当前歌曲位置
                editor.commit();

                //将当前播放音乐对象信息传递到下一个界面
                // 定义Intent对象，跳转到播放界面
                Intent intent = new Intent(PlayMusicListActivity.this, ShowContentActivity.class);
                //添加一系列要传递的播放歌曲的数据
                // 必要的
                intent.putExtra("albumName",currentAlbumName+"");//当前专辑名称
                intent.putExtra("listPosition", listPosition + "");//当前歌曲在播放列表中的位置
                intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);//传递过去播放状态为重新开始播放状态
                startActivity(intent);
            }

    }

    private void ClearData() {

        File file = new File("/data/data/"
                + PlayMusicListActivity.this.getPackageName().toString() + "/shared_prefs",
                "PlayMusicListSharePreferenceFile.xml");

        if (file.exists()) {

            SharedPreferences sharedata = PlayMusicListActivity.this.getApplication().getSharedPreferences("PlayMusicListSharePreferenceFile", Context.MODE_PRIVATE);
            sharedata.edit().clear().commit();
            file.delete();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_list_comeback_ImageView:
                PlayMusicListActivity.this.finish();
                break;
            case R.id.jumpPlayContentPageImg_id:

                SharedPreferences pref2 = PlayMusicListActivity.this.getSharedPreferences("currentPlayAlbumDataFile",PlayMusicListActivity.this.MODE_PRIVATE);
                Integer tempNowPlayId = pref2.getInt("currentPlayMusicId",-1);
                if(tempNowPlayId==-1){
                    Toast.makeText(PlayMusicListActivity.this,"当前无正在播放歌曲",Toast.LENGTH_SHORT).show();
                }else{
                    //将当前播放音乐对象信息传递到下一个界面
                    // 定义Intent对象，跳转到播放界面
                    Intent intent = new Intent(PlayMusicListActivity.this, ShowContentActivity.class);
                    //添加一系列要传递的播放歌曲的数据
                    // 必要的
                    intent.putExtra("albumName", currentAlbumName + "");//当前专辑名称
                    SharedPreferences pref = PlayMusicListActivity.this.getSharedPreferences("currentPlayAlbumDataFile",PlayMusicListActivity.this.MODE_PRIVATE);
                    Integer nowPlayId = pref.getInt("currentPlayMusicId", -1);
                    intent.putExtra("listPosition", nowPlayId + "");//当前歌曲在播放列表中的位置
                    intent.putExtra("MSG", AppConstant.PlayerMsg.CONTINUE_MSG);//传递过去播放状态为重新开始播放状态
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // Log.v("FairyDebug", "PlayMusicListActivity --------OnDestroy() ");
        AppCmpaActivityCollector.removeAppCompatActivity(this);
    }
}
