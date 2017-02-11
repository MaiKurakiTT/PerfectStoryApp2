package cn.jinmiao.bbs.perfectstoryapp.main.play;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.AddCollectionBean;
import cn.jinmiao.bbs.perfectstoryapp.bean.CancelCollectionBean;
import cn.jinmiao.bbs.perfectstoryapp.bean.CheckVIPResultBean;
import cn.jinmiao.bbs.perfectstoryapp.bean.DescrptCollection;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;

import cn.jinmiao.bbs.perfectstoryapp.bean.UserInfo;
import cn.jinmiao.bbs.perfectstoryapp.db.HistoryPlayDataBase;
import cn.jinmiao.bbs.perfectstoryapp.service.MyPlayService;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppConstant;
import cn.jinmiao.bbs.perfectstoryapp.utils.MediaUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyGetHttpUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyHttpCallbackListenerUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyPostHttpUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.SharePreferenceObjectTools;

public class ShowContentActivity extends AppCompatActivity{

    private List<MP3Info> mp3InfoList=new ArrayList<MP3Info>();//歌曲播放列表
    private MP3Info mp3Info;//当前歌曲信息

    private String albumName;//当前专辑名称
    private String playMusicName;//当前播放歌曲名称
    private String playMusicPath;//当前播放歌曲路径
    private String url;//歌曲播放实际路径
    private long duration; // 歌曲真实长度
    private int listPosition; // 播放歌曲在歌曲列表中的位置
    private boolean isPause; //暂停标识
    private int flag; // 播放标识
    private int currentTime;
    private boolean isPlaying; // 正在播放

    private TextView music_name_TextView;//歌名
    private TextView music_author_TextView;//歌手
    private Button playOrPauseBtn;// 播放暂停按钮
    private Button previous_music_button;//上一首歌曲
    private Button nextMusicBtn;//下一首歌曲
    private SeekBar music_player_SeekBar;// 音乐播放进度条
    private TextView currentProgressTextView;//当前播放进度
    private TextView finalProgressTextView;//最终进度条位置
    private ImageView comebackImgBtn;//播放返回按钮
    private ImageView collectionImageView;//收藏按钮

    private MyPlayService myPlayService;
    private PlayerReceiver playerReceiver;//定义播放接收器
    private boolean isCollectionMusic=false;//默认为false,表示没有被收藏

    //服务要发送的一些Action
    public static final String UPDATE_ACTION = "com.xingyun.action.UPDATE_ACTION"; // 更新动作
    public static final String CTL_ACTION = "com.xingyun.action.CTL_ACTION"; // 控制动作
    public static final String MUSIC_CURRENT = "com.xingyun.action.MUSIC_CURRENT"; // 音乐当前时间改变动作
    public static final String MUSIC_DURATION = "com.xingyun.action.MUSIC_DURATION";// 音乐播放长度改变动作
    public static final String MUSIC_PLAYING = "com.xingyun.action.MUSIC_PLAYING"; // 音乐正在播放动作
    public static final String REPEAT_ACTION = "com.xingyun.action.REPEAT_ACTION"; // 音乐重复播放动作
    public static final String SHUFFLE_ACTION = "com.xingyun.action.SHUFFLE_ACTION";// 音乐随机播放动作

    private AudioManager am;		//音频管理引用，提供对音频的控制
    RelativeLayout ll_player_voice;	//音量控制面板布局
    int currentVolume;				//当前音量
    int maxVolume;					//最大音量
    ImageButton ibtn_player_voice;	//显示音量控制面板的按钮
    SeekBar sb_player_voice;		//控制音量大小
    // 音量面板显示和隐藏动画
    private Animation showVoicePanelAnimation;
    private Animation hiddenVoicePanelAnimation;

    private String isCheckCollectionJSONData;//返回是否收藏json数据
    private String executeCollectionJSONData;//执行收藏返回JSON数据
    private String checkVIPResultStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCmpaActivityCollector.addAppCompatActivity(this);
        setContentView(R.layout.activity_show_content);

        findViewById();//找界面Id
        GetPlayMusicInfo();//获取传过来的数据
        registerReceiver();//注册广播接收器 控制播放模式和下一曲等方法
        setPhoneOnCallMethodOrVoiceManageMethod();

        checkIsExpiredMethod();


    }


    //检查是否过期
    private void checkIsExpiredMethod() {

       // Log.v("FairyDebug1","检查是否过期-------------------------start method----------------------");
        SharedPreferences pref = getSharedPreferences("TempUserUIdFile", MODE_PRIVATE);
        int tempUserUid = pref.getInt("tempUserUid",-1);

        if(tempUserUid==-1)
        {
           // Log.v("FairyDebug1","   用户ID=-1-----------");
            return ;
        }
        MyGetHttpUtil.mySendHttpRequestUtils(getString(R.string.BBSHome) + "?uid=" + tempUserUid, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                checkVIPResultStr=response;
                checkVIPIsExpiredHandler.sendEmptyMessage(55);
                Log.v("FairyDebug1", "会员是否过期返回JSON数据" + checkVIPResultStr);
            }

            @Override
            public void onError(Exception e) {
                checkVIPIsExpiredHandler.sendEmptyMessage(66);
            }
        });

      //  Log.v("FairyDebug1", "检查是否过期-------------------------end method----------------------");
    }

    private android.os.Handler checkVIPIsExpiredHandler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 55:

                    Gson checkVIPGSON = new Gson();
                    CheckVIPResultBean VIPIsExpriedBean=checkVIPGSON.fromJson(checkVIPResultStr, CheckVIPResultBean.class);
                   //  Log.v("FairyDebug1","调试输出"+VIPIsExpriedBean.isStatus()+"");

                    if(VIPIsExpriedBean.isStatus())
                    {
                        SharedPreferences.Editor editor = getSharedPreferences("checkVIP_FIlE", MODE_PRIVATE).edit();
                        editor.putBoolean("VIP_IS_EXPIRED",true);
                        editor.commit();
                     //   Log.v("FairyDebug","VIP没有过期："+checkVIPResultStr+"");
                    }else if(!VIPIsExpriedBean.isStatus()){
                      //  Log.v("FairyDebug", "VIP过期：" + checkVIPResultStr +"");
                        SharedPreferences.Editor editor = getSharedPreferences("checkVIP_FIlE", MODE_PRIVATE).edit();
                        editor.putBoolean("VIP_IS_EXPIRED", false);
                        editor.commit();
                    }

                    break;
                case 66:
                   // Log.v("FairyDebug1","服务器响应失败");
                   // Toast.makeText(ShowContentActivity.this,"网络连接失败,请检查网络设置",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    //获取上一个界面的信息
    private void GetPlayMusicInfo() {
        //获取上一界面传递过来的数据
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        albumName=bundle.getString("albumName");//当前播放专辑名称
        String listPosition2 = bundle.getString("listPosition");
        listPosition=Integer.valueOf(listPosition2);//当前选中播放歌曲位置
        flag = bundle.getInt("MSG");//播放标识




        if(flag == AppConstant.PlayerMsg.PLAY_DOWNLOAD){
           // String musicname = bundle.getString("musicname");
            mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");
            music_name_TextView.setText(mp3InfoList.get(listPosition).getSubject());
            music_author_TextView.setText(mp3InfoList.get(listPosition).getSubject());
            url = mp3InfoList.get(listPosition).getMp3_path();
            playMusicName = mp3InfoList.get(listPosition).getSubject();
            playMusicPath = mp3InfoList.get(listPosition).getMp3_path();
            executePlayerByServiceMethod(url);
            return;
        }



        //读取当前播放歌曲列表
        mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");
        mp3Info = mp3InfoList.get(listPosition);//获取当前播放的歌曲对象


//        HistoryPlayDataBaseTools historyPlayDataBaseTools=new HistoryPlayDataBaseTools();
//
//        historyPlayDataBaseTools.addHistoryMusic(ShowContentActivity.this,mp3Info);

        SharedPreferences pref = getSharedPreferences("TempUserUIdFile", MODE_PRIVATE);
        int tempUserUid = pref.getInt("tempUserUid",-1);

        //如果用户登录后才记录播放历史
        if(tempUserUid!=-1)
        {
           // Log.v("FairyDebug1","已登陆用户-------有权利记录-----最近播放记录-----------");
            addHistoryMusic(mp3Info);
        }

        playMusicName = mp3Info.getSubject();//歌曲名称
        playMusicPath = mp3Info.getMp3_path();//歌曲播放路径

        //测试
//        Log.v("FairyDebug","ShowContentActivity------获取上一界面信息----当前播放列表长度为-------="+mp3InfoList.size());
//        Log.v("FairyDebug","ShowContentActivity------获取上一界面信息----当前播放专辑名称-------="+albumName);
//        Log.v("FairyDebug","ShowContentActivity------获取上一界面信息---------当前播放歌曲名称为-------="+playMusicName);
//        Log.v("FairyDebug", "ShowContentActivity------获取上一界面信息-------当前播放歌曲路径为-------=" + playMusicPath);
//        Log.v("FairyDebug", "ShowContentActivity------获取上一界面信息------------当前播放位置为-------=" + listPosition);
//        Log.v("FairyDebug", "ShowContentActivity------获取上一界面信息-------当前播放标识为-------=" + flag);

        //更新UI上正在播放的歌曲名称
        music_name_TextView.setText(playMusicName);
        music_author_TextView.setText(albumName);

        //构造播放地址
        url = getString(R.string.MP3PlayHomeAddress)+playMusicPath; // url = "http://mp3.jinmiao.cn/mp3file/01tonghua/11/acndxhg0np.mp3";
        //调试构造的播放地址
       // Log.v("FairyDebug", "ShowContentActivity------获取上一界面信息-----------构造当前播放歌曲路径为-------=" + url);

        //从界面传递过来从头播放信号
        if(flag== AppConstant.PlayerMsg.PLAY_MSG) {
            //执行服务播放音乐监听方法
            executePlayerByServiceMethod(url);
        }else if(flag==AppConstant.PlayerMsg.CONTINUE_MSG){
            executeContinuePlayByServiceMethod(url);
        }
    }


    public void addHistoryMusic(MP3Info mp3Info){

        ContentValues historyMusicContentValues;

        //打开数据库
        HistoryPlayDataBase historyMusicDBHelper=new HistoryPlayDataBase(ShowContentActivity.this,"HistoryMusic.db",null,1);
        SQLiteDatabase historyMusicDB = historyMusicDBHelper.getReadableDatabase();

        Cursor mCursor=null;

        historyMusicDB.beginTransaction();

        try {
            mCursor = historyMusicDB.rawQuery(
                    "select * from t_history_music where history_music_id=?",
                    new String[] { (mp3Info.getId() + "").trim() });

            if(!mCursor.moveToFirst())//数据库没有查询到数据
            {
                historyMusicContentValues = new ContentValues();
                historyMusicContentValues.put("history_music_id",mp3Info.getId());
                historyMusicContentValues.put("history_music_subject",mp3Info.getSubject());
                historyMusicContentValues.put("history_music_path",mp3Info.getMp3_path());

                historyMusicDB.insert("t_history_music",null,historyMusicContentValues);
                historyMusicDB.setTransactionSuccessful();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            mCursor.close();
            historyMusicDB.endTransaction();
            historyMusicDB.close();
            Log.v("FairyDebug","保存完成");
        }

    }


    private void MyCheckCollection(MP3Info mp3Info) {
        //读取本地登录用户数据 检查权限
        UserInfo mCurrentLoginUser = (UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(ShowContentActivity.this, "LoginUserShareference", "LoginUserShareferenceKey");
        if(mCurrentLoginUser!=null)//用户已经登陆 检查是否收藏
        {
            Integer musicId=Integer.valueOf(mp3Info.getId());
            checkIsCollection(musicId);
        }
    }


    private void checkIsCollection(Integer musicId) {

       // Log.v("FairyDebug","ShowContentActivity--------------正在播放歌曲ID=-------"+musicId);

        SharedPreferences pref = getSharedPreferences("TempUserUIdFile", MODE_PRIVATE);
        int tempUserUid = pref.getInt("tempUserUid",-1);

        //Log.v("FairyDebug","ShowContentActivity--------------检查当前歌曲是否被收藏接口URL---"+getString(R.string.getMusicListURL)+"?action=isCollection&UID="+tempUserUid+"&MID="+musicId);
        MyGetHttpUtil.mySendHttpRequestUtils(getString(R.string.getMusicListURL)+"?action=isCollection&UID="+tempUserUid+"&MID="+musicId, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                isCheckCollectionJSONData=response;
              //  Log.v("FairyDebug", "ShowContentActivity----------检查是否收藏请求返回JSON数据--------------------" +isCheckCollectionJSONData);
                checkIsCollectionHandler.sendEmptyMessage(10);
            }

            @Override
            public void onError(Exception e) {
                checkIsCollectionHandler.sendEmptyMessage(20);
            }
        });
    }

    private android.os.Handler checkIsCollectionHandler=new android.os.Handler(){

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 10:
                   try {
                       Gson collectionGson = new Gson();
                       DescrptCollection descrptCollection=collectionGson.fromJson(isCheckCollectionJSONData, DescrptCollection.class);
                      // Log.v("FairyDebug","ShowContentActivity----------------是否被收藏="+descrptCollection.getIsCollection());
                       if(descrptCollection.getIsCollection().equals("no"))
                       {
                           collectionImageView.setImageResource(R.drawable.no_collection);
                           isCollectionMusic=false;
                       }else if(descrptCollection.getIsCollection().equals("yes")){
                           collectionImageView.setImageResource(R.drawable.collection_btn);
                           isCollectionMusic=true;
                       }
                   } catch (JsonSyntaxException e) {
                      // Log.v("FairyDebug", "ShowContentActivity----------------检擦是否收藏解析异常-----------" );
                   }
                   break;
               case 20:
                   //Toast.makeText(ShowContentActivity.this,"网络连接失败,请检查网络设置",Toast.LENGTH_SHORT).show();
                   break;
               default:
                   break;
           }
        }
    };

    //执行继续播放方法
    private void executeContinuePlayByServiceMethod(String url) {
        //更新UI显示图片为暂停按钮
        playOrPauseBtn.setBackgroundResource(R.drawable.pause_selector);
        isPause=true;//当前显示图片为暂停

        SetPlayBySequential();// 默认设置按顺序播放

        //读取当前歌曲
        SharedPreferences pref = ShowContentActivity.this.getSharedPreferences("currentPlayAlbumDataFile",ShowContentActivity.this.MODE_PRIVATE);
        int  progress= pref.getInt("currentPlayerTime",0);

        //启动播放服务 传递播放地址和位置
        Intent intent = new Intent();
        intent.setAction("com.xingyun.media.MUSIC_SERVICE");
        intent.putExtra("url", url);
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("MSG", AppConstant.PlayerMsg.PROGRESS_CHANGE);
        intent.putExtra("progress", progress);
        intent.setPackage(getPackageName());
        startService(intent);
       // Log.v("FairyDebug", "---------PlayMusicListActivity has been start  form ShowContentActivity-------------");
    }

    //执行播放方法
    private void executePlayerByServiceMethod(String url) {

        //更新UI显示图片为暂停按钮
        playOrPauseBtn.setBackgroundResource(R.drawable.pause_selector);
        isPause=true;//当前显示图片为暂停

        SetPlayBySequential();// 默认设置按顺序播放

        //启动播放服务 传递播放地址和位置
        Intent intent = new Intent();// 后台启动MyPlayService服务
        intent.setAction("com.xingyun.media.MUSIC_SERVICE");//找到音乐播放服务
        intent.putExtra("url", url);//音乐播放地址
        intent.putExtra("listPosition",listPosition);//当前播放列表中第几个音乐
        intent.putExtra("MSG",AppConstant.PlayerMsg.PLAY_MSG);//播放状态标识
        intent.setPackage(getPackageName());
        startService(intent);
       // Log.v("FairyDebug", "---------PlayMusicListActivity has been start  form ShowContentActivity-------------");
    }
    //设置播放默认为顺序播放
    private void SetPlayBySequential() {
        Intent intent = new Intent(CTL_ACTION);//修改播放模式
        intent.putExtra("control", 2);//2表示全部循环播放
        sendBroadcast(intent);//发送服务广播
    }




    /**
     * 活动准备好和用户进行交互的时候调用
     * */
    @Override
    protected void onResume() {
       // Log.v("FairyDebug", "ShowContentActivity -----------播放界面可见后广播被注册---获取播放服务的一切改变消息");
        registerReceiver();//注册广播接收器 控制播放模式和下一曲等方法

        //读取本地登录用户数据 检查权限
        UserInfo mCurrentLoginUser = (UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(ShowContentActivity.this, "LoginUserShareference", "LoginUserShareferenceKey");
        if(mCurrentLoginUser!=null)//用户已经登陆 检查是否收藏
        {
            //读取当前播放歌曲列表
            mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");

            //读取本地文件获取当前歌曲播放位置
            SharedPreferences pref3 = ShowContentActivity.this.getSharedPreferences("currentPlayAlbumDataFile",ShowContentActivity.this.MODE_PRIVATE);
            Integer tempPosition = pref3.getInt("currentPlayMusicId",0);//当前播放的是第几首歌
            mp3Info = mp3InfoList.get(tempPosition);//获取当前播放的歌曲对象

            //执行收藏功能必须是登录用户
            MyCheckCollection(mp3Info);
        }



        super.onResume();
    }
    /**
     * onStop()
     * 活动完全不可见的时候调用,系统内存不足时可能会被回收。
     * **/
    @Override
    protected void onStop() {
        super.onStop();
        //销毁时调用反注册广播   并不会停止播放音乐
        unregisterReceiver(playerReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCmpaActivityCollector.removeAppCompatActivity(this);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 键盘码如果是返回键 则返回上一页面
        if(keyCode==KeyEvent.KEYCODE_BACK){
            ShowContentActivity.this.finish();
            SharedPreferences sp = getSharedPreferences("init",MODE_PRIVATE);
        }
        return super.onKeyDown(keyCode, event);
    }





    // 定义和注册广播接收器
    private void registerReceiver() {
        // TODO Auto-generated method stub
        //定义和注册播放广播接收器
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION); // 更新动作 播放进度
        filter.addAction(MUSIC_CURRENT);// 音乐当前时间改变动作
        filter.addAction(MUSIC_DURATION);// 音乐播放长度改变动作
        registerReceiver(playerReceiver, filter);
    }

    private void findViewById() {
        //歌曲名称
        music_name_TextView = (TextView) findViewById(R.id.music_name_TextView_id);
        music_author_TextView = (TextView) findViewById(R.id.music_author_TextView_id);

        ButtonClickListener listener = new ButtonClickListener();

        //播放或暂停按钮
        playOrPauseBtn = (Button) findViewById(R.id.playOrPause_music_btn_id);
        playOrPauseBtn.setOnClickListener(listener);

        //音乐播放进度条
        music_player_SeekBar = (SeekBar) findViewById(R.id.audioTrack);
       music_player_SeekBar.setOnSeekBarChangeListener(new MySeekBarChangeListener());
        //music_player_SeekBar.setOnSeekBarChangeListener(null);

        //下一首歌曲
        nextMusicBtn=(Button)findViewById(R.id.next_music_Button_id);
        nextMusicBtn.setOnClickListener(listener);

        //上一首歌曲
        previous_music_button=(Button)findViewById(R.id.previous_music_button_id);
        previous_music_button.setOnClickListener(listener);

        currentProgressTextView=(TextView)findViewById(R.id.current_progress);
        finalProgressTextView=(TextView)findViewById(R.id.final_progress);

        //声音按钮
        ibtn_player_voice = (ImageButton) findViewById(R.id.ibtn_player_voice);
        ibtn_player_voice.setOnClickListener(listener);
        //声音布局
        ll_player_voice = (RelativeLayout) findViewById(R.id.ll_player_voice);
        //声音进度条
        sb_player_voice = (SeekBar) findViewById(R.id.sb_player_voice);
        sb_player_voice.setOnSeekBarChangeListener(new MySeekBarChangeListener());

        comebackImgBtn=(ImageView)findViewById(R.id.nav_play_comeback_id);
        comebackImgBtn.setOnClickListener(listener);

        collectionImageView=(ImageView)findViewById(R.id.my_collection_ImageView);
        collectionImageView.setOnClickListener(listener);

    }

    /**
     * 实现监听SeeKBar的类
     *
     * @author fairy
     *
     */
    private class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            switch(seekBar.getId()) {
                case R.id.audioTrack:
                    if (fromUser) {
                        audioTrackChange(progress); // 用户控制进度的改变
                    }
                    break;
                case R.id.sb_player_voice:
                    // 设置音量
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    break;
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    }

    /**
     * 播放进度改变
     * @param progress
     */
    public void audioTrackChange(int progress) {
        Intent intent = new Intent();
        intent.setAction("com.xingyun.media.MUSIC_SERVICE");
        intent.putExtra("url", url);
        intent.putExtra("listPosition", listPosition);
        //
        intent.putExtra("MSG", AppConstant.PlayerMsg.PROGRESS_CHANGE);
        intent.putExtra("progress", progress);
        intent.setPackage(getPackageName());
        startService(intent);
    }

    //界面按钮单击监听器
    private final class ButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            UserInfo mCurrentLoginUser;
            switch (v.getId()) {
                case R.id.playOrPause_music_btn_id://播放或暂停按钮被单击触发
                    if (isPause)//执行歌曲暂停 显示播放按钮
                    {
                        playOrPauseBtn.setBackgroundResource(R.drawable.play_selector);
                        intent.setAction("com.xingyun.media.MUSIC_SERVICE");
                        intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
                        intent.setPackage(getPackageName());
                        startService(intent);
                        isPause = false;
                    } else {//执行歌曲播放 显示暂停按钮
                        playOrPauseBtn.setBackgroundResource(R.drawable.pause_selector);
                        intent.setAction("com.xingyun.media.MUSIC_SERVICE");
                        intent.putExtra("MSG", AppConstant.PlayerMsg.CONTINUE_MSG);
                        intent.setPackage(getPackageName());
                        startService(intent);
                        isPause = true;
                    }
                    break;
                case R.id.next_music_Button_id:
                    next_music();
                    break;
                case R.id.previous_music_button_id:
                    previous_music();
                    break;
                case R.id.ibtn_player_voice:
                    //voicePanelAnimation();
                    //// TODO: 2016/12/19 0019  点击事件 音量按钮
                   /* String name = BASE_PATH + playMusicPath.replace("/","");
                    //mnt/sdcard/01tonghua/11/acndxhg0np.mp3
                    String n = name.replace("mp3","psa");
                    MP3Info mp3Info = mp3InfoList.get(listPosition);
                    url=getString(R.string.MP3PlayHomeAddress)+"/"+mp3Info.getMp3_path();
                    downloadMusic(getString(R.string.MP3PlayHomeAddress) + playMusicPath ,n );*/
                    mCurrentLoginUser = (UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(ShowContentActivity.this, "LoginUserShareference", "LoginUserShareferenceKey");
                    //如果无登陆即mCurrentLoginUser=null 登录无权限 mCurrentLoginUser.getVgid().equals("0")

                    SharedPreferences pref = ShowContentActivity.this.getSharedPreferences("checkVIP_FIlE",ShowContentActivity.this.MODE_PRIVATE);
                    boolean   vipIsExpired= pref.getBoolean("VIP_IS_EXPIRED",false);// 如果为false 执行禁止播放，否则正常播放
                    if (mCurrentLoginUser == null ||mCurrentLoginUser.getVgid()==null || (!vipIsExpired) ) {
                        Toast.makeText(ShowContentActivity.this,"非会员暂不可以下载",Toast.LENGTH_SHORT).show();
                    }else{
                        String name = BASE_PATH + mp3Info.getMp3_path().replace("/","");
                        String n = name.replace("mp3","psa");
                        MP3Info mp3Info = mp3InfoList.get(listPosition);
                        if(mp3Info.getMp3_path().contains("http")){
                            url = mp3Info.getMp3_path();
                        }else {
                            url=getString(R.string.MP3PlayHomeAddress)+mp3Info.getMp3_path();
                        }


                        downloadMusic(url,n);
                    }

                    break;
                case R.id.nav_play_comeback_id:
                    ShowContentActivity.this.finish();
                    break;
                case R.id.my_collection_ImageView:
                    //读取本地登录用户数据 检查权限
                    mCurrentLoginUser = (UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(ShowContentActivity.this, "LoginUserShareference", "LoginUserShareferenceKey");
                    if(mCurrentLoginUser!=null)//用户已经登陆 检查是否收藏
                    {
                        changeCollectionImageView();
                    }else{
                        Toast.makeText(ShowContentActivity.this,"收藏功能只对登录用户开放",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }
    private static final String BASE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator +"jmpfa" + File.separator;
    private ProgressDialog progressDialog;
    private void downloadMusic(final String url, final String path) {
        SharedPreferences sp = getSharedPreferences("music",MODE_PRIVATE);
        int size = sp.getAll().size();
        if(size >= 6){
            sp.edit().remove(sp.getAll().keySet().iterator().next()).commit();
            Toast.makeText(ShowContentActivity.this,"已经清空之前下载...",Toast.LENGTH_SHORT).show();
            recursionDeleteFile(new File(BASE_PATH));

        }

        String isDown = sp.getString(url,null);
        if(isDown != null){
            Toast.makeText(ShowContentActivity.this,"该歌曲已下载",Toast.LENGTH_SHORT).show();
            return ;
         }else{
            progressDialog = new ProgressDialog(this);
            RequestParams requestParams = new RequestParams(url);
            getSharedPreferences("music",MODE_PRIVATE).edit().putString(url,playMusicName).commit();
            requestParams.setSaveFilePath(path);
            x.http().get(requestParams, new Callback.ProgressCallback<File>() {
                @Override
                public void onWaiting() {
                }

                @Override
                public void onStarted() {
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setMessage("亲，努力下载中。。。");
                    progressDialog.show();
                    progressDialog.setMax((int) total);
                    progressDialog.setProgress((int) current);
                }

                @Override
                public void onSuccess(File result) {

                    progressDialog.dismiss();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ex.printStackTrace();
                    Toast.makeText(ShowContentActivity.this, "下载失败，请检查网络和SD卡", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });
        }




    }

    public void recursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                recursionDeleteFile(f);
            }
            file.delete();
        }
    }

    private void changeCollectionImageView() {

//        Log.v("FairyDebug1","ShowContentActivity--------------------------------当前用户已经登陆,有权使用收藏功能");
//        Log.v("FairyDebug1","-----------------------------start----------------------------------------------------------");
        Toast.makeText(ShowContentActivity.this,"当前用户已经登陆有权使用收藏功能",Toast.LENGTH_SHORT);
        SharedPreferences pref = getSharedPreferences("TempUserUIdFile", MODE_PRIVATE);
        int tempUserUid = pref.getInt("tempUserUid",-1);
       // Log.v("FairyDebug1","ShowContentActivity-----------------当前用户UId="+tempUserUid);

        //读取当前播放歌曲列表
        mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");

        //读取本地文件获取当前歌曲播放位置
        SharedPreferences pref2 = ShowContentActivity.this.getSharedPreferences("currentPlayAlbumDataFile",ShowContentActivity.this.MODE_PRIVATE);
        Integer tempPosition = pref2.getInt("currentPlayMusicId",0);//当前播放的是第几首歌
      //  Log.v("FairyDebug1","ShowContentActivity-----------------当前歌曲播放位置="+mp3InfoList.get(tempPosition).getId());
       if(isCollectionMusic){//已经被收藏 点击后取消收藏

           MyPostHttpUtil.myPostHttpRequestUtils(getString(R.string.getMusicListURL) + "?", "action=cancelCollection&UID=" + tempUserUid + "&MID=" + mp3InfoList.get(tempPosition).getId(), new MyHttpCallbackListenerUtil() {
               @Override
               public void onFinish(String response) {
               //    Log.v("FairyDebug", "ShowContentActivity----------取消收藏请求返回JSON数据--------------------" + response);
                   executeCollectionJSONData=response;
                   cancelCollectionHandler.sendEmptyMessage(1);
               }

               @Override
               public void onError(Exception e) {
                   cancelCollectionHandler.sendEmptyMessage(2);
               }
           });


       }else if(!isCollectionMusic){// 默认没有被收藏 点击后变为收藏

           MyPostHttpUtil.myPostHttpRequestUtils(getString(R.string.getMusicListURL) + "?", "action=addCollection&UID=" + tempUserUid + "&MID=" + mp3InfoList.get(tempPosition).getId(), new MyHttpCallbackListenerUtil() {
               @Override
               public void onFinish(String response) {
                   executeCollectionJSONData=response;
                 //  Log.v("FairyDebug", "ShowContentActivity----------增加收藏请求返回JSON数据--------------------" + response);
                   addCollectionHandler.sendEmptyMessage(1);
               }

               @Override
               public void onError(Exception e) {
                   addCollectionHandler.sendEmptyMessage(2);
               }
           });


       }
    }

    private android.os.Handler cancelCollectionHandler=new android.os.Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    try {
                        Gson cancelCollection=new Gson();
                        CancelCollectionBean cancelCollectionBean=cancelCollection.fromJson(executeCollectionJSONData, CancelCollectionBean.class);
                        if(cancelCollectionBean.getCancleCollection().equals("success")){
                            collectionImageView.setImageResource(R.drawable.no_collection);
                            isCollectionMusic=false;
                            Toast.makeText(ShowContentActivity.this,"收藏已取消",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ShowContentActivity.this,"收藏取消失败",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JsonSyntaxException e) {
                        Toast.makeText(ShowContentActivity.this,"数据解析异常-1",Toast.LENGTH_SHORT).show();;
                    }
                    break;
                case 2:
                    Toast.makeText(ShowContentActivity.this,"收藏取消失败",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    private android.os.Handler addCollectionHandler=new android.os.Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    try {
                        Gson addCollectionGson = new Gson();
                        AddCollectionBean addCollection=addCollectionGson.fromJson(executeCollectionJSONData, AddCollectionBean.class);
                        if(addCollection.getAddCollection().equals("success")){
                            collectionImageView.setImageResource(R.drawable.collection_btn);
                            isCollectionMusic=true;
                            Toast.makeText(ShowContentActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ShowContentActivity.this,"收藏失败",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JsonSyntaxException e) {
                        Toast.makeText(ShowContentActivity.this,"添加收藏结果解析异常-1",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(ShowContentActivity.this,"收藏失败",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };




    /**
     * 上一首
     */
    public void previous_music() {

        //当前播放位置-1
        listPosition = listPosition - 1;
        //如果当前播放歌曲不是第一个
        if (listPosition >= 0) {

            //当点击上一首按钮后 图片显示暂停 执行播放功能
            playOrPauseBtn.setBackgroundResource(R.drawable.pause_selector);
            //获取当前要播放的歌曲
            MP3Info mp3Info = mp3InfoList.get(listPosition); // 上一首MP3




            // 保存实现持久化操作
            SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
            editor.putInt("currentPlayMusicId",Integer.valueOf(listPosition));//当前播放歌曲处于播放列表位置
            editor.commit();
           // showArtwork(mp3Info);//显示专辑封面
            music_name_TextView.setText(mp3Info.getSubject());
            url = getString(R.string.MP3PlayHomeAddress)+"/"+mp3Info.getMp3_path();//获取当前歌曲播放路径

            //启动音乐播放服务
            Intent intent = new Intent();
            intent.setAction("com.xingyun.media.MUSIC_SERVICE");
            intent.putExtra("url", url);
            intent.putExtra("listPosition", listPosition);//传递当前播放地址
            intent.putExtra("MSG", AppConstant.PlayerMsg.PRIVIOUS_MSG);//通知服务播放上一首歌
            intent.setPackage(getPackageName());
            startService(intent);

            //读取本地登录用户数据 检查权限
            UserInfo mCurrentLoginUser = (UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(ShowContentActivity.this, "LoginUserShareference", "LoginUserShareferenceKey");
            if(mCurrentLoginUser!=null)//用户已经登陆 检查是否收藏
            {
                //读取当前播放歌曲列表
                mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");

                //读取本地文件获取当前歌曲播放位置
                SharedPreferences pref3 = ShowContentActivity.this.getSharedPreferences("currentPlayAlbumDataFile", ShowContentActivity.this.MODE_PRIVATE);
                Integer tempPosition = pref3.getInt("currentPlayMusicId",0);//当前播放的是第几首歌
                mp3Info = mp3InfoList.get(tempPosition);//获取当前播放的歌曲对象

                //保存播放记录插入到数据库
                addHistoryMusic(mp3Info);

                //执行收藏功能必须是登录用户
                MyCheckCollection(mp3Info);
            }
        } else {
            listPosition = 0;
            // 保存实现持久化操作
            SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
            editor.putInt("currentPlayMusicId",Integer.valueOf(listPosition));//当前播放专辑Id  这个Id可以获取当前播放列表
            editor.commit();
            Toast.makeText(ShowContentActivity.this, "没有上一首了", Toast.LENGTH_SHORT).show();

            //读取当前播放歌曲列表
            mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");

            //读取本地文件获取当前歌曲播放位置
            SharedPreferences pref3 = ShowContentActivity.this.getSharedPreferences("currentPlayAlbumDataFile", ShowContentActivity.this.MODE_PRIVATE);
            Integer tempPosition = pref3.getInt("currentPlayMusicId", 0);//当前播放的是第几首歌
            mp3Info = mp3InfoList.get(tempPosition);//获取当前播放的歌曲对象



            //读取本地登录用户数据 检查权限
            UserInfo mCurrentLoginUser = (UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(ShowContentActivity.this, "LoginUserShareference", "LoginUserShareferenceKey");
            if(mCurrentLoginUser!=null)//用户已经登陆 检查是否收藏
            {
                //保存播放记录插入到数据库
                addHistoryMusic(mp3Info);
                //执行收藏功能必须是登录用户
                MyCheckCollection(mp3Info);

            }
        }
    }


    /**
     * 下一首
     */
    public void next_music() {

        listPosition = listPosition + 1;
        if (listPosition <= mp3InfoList.size() - 1) {
            playOrPauseBtn.setBackgroundResource(R.drawable.pause_selector);

            MP3Info mp3Info = mp3InfoList.get(listPosition);
            url=getString(R.string.MP3PlayHomeAddress)+"/"+mp3Info.getMp3_path();
            music_name_TextView.setText(mp3Info.getSubject());
            playMusicName = mp3Info.getSubject();


            // 保存当前临时播放列表
//            HistoryPlayDataBaseTools historyPlayDataBaseTools=new HistoryPlayDataBaseTools();
//            historyPlayDataBaseTools.addHistoryMusic(ShowContentActivity.this, mp3Info);

            // 保存实现持久化操作
            SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
            editor.putInt("currentPlayMusicId", Integer.valueOf(listPosition));//当前播放专辑Id  这个Id可以获取当前播放列表
            editor.commit();

            //启动音乐播放服务
            Intent intent = new Intent();
            intent.setAction("com.xingyun.media.MUSIC_SERVICE");
            intent.putExtra("url",url);//获取当前音乐播放地址
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("MSG", AppConstant.PlayerMsg.NEXT_MSG);//通知服务播放下一首歌曲
            intent.setPackage(getPackageName());
            startService(intent);

            //读取本地登录用户数据 检查权限
            UserInfo mCurrentLoginUser = (UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(ShowContentActivity.this, "LoginUserShareference", "LoginUserShareferenceKey");
            if(mCurrentLoginUser!=null)//用户已经登陆 检查是否收藏
            {
                //读取当前播放歌曲列表
                mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");

                //读取本地文件获取当前歌曲播放位置
                SharedPreferences pref3 = ShowContentActivity.this.getSharedPreferences("currentPlayAlbumDataFile",ShowContentActivity.this.MODE_PRIVATE);
                Integer tempPosition = pref3.getInt("currentPlayMusicId",0);//当前播放的是第几首歌
                mp3Info = mp3InfoList.get(tempPosition);//获取当前播放的歌曲对象

                //保存播放记录插入到数据库
                addHistoryMusic(mp3Info);

                //执行收藏功能必须是登录用户
                MyCheckCollection(mp3Info);
            }

        } else {
            listPosition = mp3InfoList.size() - 1;
            // 保存实现持久化操作
            SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
            editor.putInt("currentPlayMusicId",Integer.valueOf(listPosition));//当前播放专辑Id  这个Id可以获取当前播放列表
            editor.commit();
            Toast.makeText(ShowContentActivity.this, "没有下一首了", Toast.LENGTH_SHORT).show();

            //读取本地登录用户数据 检查权限
            UserInfo mCurrentLoginUser = (UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(ShowContentActivity.this, "LoginUserShareference", "LoginUserShareferenceKey");

            //读取当前播放歌曲列表
            mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");

            //读取本地文件获取当前歌曲播放位置
            SharedPreferences pref3 = ShowContentActivity.this.getSharedPreferences("currentPlayAlbumDataFile",ShowContentActivity.this.MODE_PRIVATE);
            Integer tempPosition = pref3.getInt("currentPlayMusicId",0);//当前播放的是第几首歌
            mp3Info = mp3InfoList.get(tempPosition);//获取当前播放的歌曲对象

            //保存播放记录插入到数据库
            addHistoryMusic(mp3Info);

            if(mCurrentLoginUser!=null)//用户已经登陆 检查是否收藏
            {
                //保存播放记录插入到数据库
                addHistoryMusic(mp3Info);
                //执行收藏功能必须是登录用户
                MyCheckCollection(mp3Info);
            }
        }
    }

    /**
     * 播放广播接收器 描述：用来接收从service传回来的广播的内部类
     * 更新播放名称 更新播放时间 更新进度条
     * @author fairy
     */
    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // 音乐当前时间改变动作
            if (action.equals(MUSIC_CURRENT)) {
                currentTime = intent.getIntExtra("currentTime", -1);
                //当前进度消耗时间
                currentProgressTextView.setText(MediaUtil.formatTime(currentTime));
                music_player_SeekBar.setProgress(currentTime);
                SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
                editor.putInt("currentPlayerTime", Integer.valueOf(currentTime));//当前播放时间
                editor.commit();
            } else if (action.equals(MUSIC_DURATION)) {
                //音乐播放长度改变动作 歌曲切换时歌曲的播放总时长发生变化
                int duration = intent.getIntExtra("duration", -1);
                music_player_SeekBar.setMax(duration);//设置进度条最大值
                finalProgressTextView.setText(MediaUtil.formatTime(duration));//显示当前歌曲播放完所需时间

                //读取本地登录用户数据 检查权限
                UserInfo mCurrentLoginUser = (UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(ShowContentActivity.this, "LoginUserShareference", "LoginUserShareferenceKey");
                //读取当前播放歌曲列表
                mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(ShowContentActivity.this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");

                //读取本地文件获取当前歌曲播放位置
                SharedPreferences pref3 = ShowContentActivity.this.getSharedPreferences("currentPlayAlbumDataFile",ShowContentActivity.this.MODE_PRIVATE);
                Integer tempPosition = pref3.getInt("currentPlayMusicId",0);//当前播放的是第几首歌
                mp3Info = mp3InfoList.get(tempPosition);//获取当前播放的歌曲对象



                // 保存当前临时播放列表
//                HistoryPlayDataBaseTools historyPlayDataBaseTools=new HistoryPlayDataBaseTools();
//                historyPlayDataBaseTools.addHistoryMusic(ShowContentActivity.this, mp3Info);

                if(mCurrentLoginUser!=null)//用户已经登陆 检查是否收藏
                {
                    //保存播放记录插入到数据库
                    addHistoryMusic(mp3Info);
                    //执行收藏功能必须是登录用户
                    MyCheckCollection(mp3Info);
                }


            } else if (action.equals(UPDATE_ACTION)) {
                // 更新动作 歌曲位置变化
                // 获取Intent中的current消息，current代表当前正在播放的歌曲
                listPosition = intent.getIntExtra("current", -1);
                if (listPosition >= 0) {
                    music_name_TextView.setText(mp3InfoList.get(listPosition).getSubject());
                }
            }
        }
    }
    //监听事件  来电事件和音量管理
    private void setPhoneOnCallMethodOrVoiceManageMethod() {
        // 添加来电监听事件
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); // 获取系统服务
        /**
         * TelephonyManager 提供了一种获取手机信息的方式，包括通话状态、网络连接、运营商信息等
         * getSystemService(Context.TELEPHONY_SERVICE);获取系统服务
         * */
        telManager.listen(new MyMobliePhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);

        //音量调节面板显示和隐藏的动画
        showVoicePanelAnimation = AnimationUtils.loadAnimation(ShowContentActivity.this, R.anim.push_up_in);
        hiddenVoicePanelAnimation = AnimationUtils.loadAnimation(ShowContentActivity.this, R.anim.push_up_out);
        //获得系统音频管理服务对象
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);//获取当前音量
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//获取系统最大音量
        sb_player_voice.setProgress(currentVolume);//当前音量大小设置成系统当前音量大小
        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);//把当前声音大小设置进来
    }
    /** 电话监听器类  */
    private class MyMobliePhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            //根据电话接听状态做出不同的处理
            switch (state) {
                //通话和响铃状态修改播放状态
                case TelephonyManager.CALL_STATE_RINGING:	//响铃状态
                case TelephonyManager.CALL_STATE_OFFHOOK:	//通话状态
                    //启动播放服务
                    Intent intent2 = new Intent(ShowContentActivity.this,MyPlayService.class);
                    intent2.setAction("com.xingyun.media.MUSIC_SERVICE");//这个参数在清单文件中
                    intent2.putExtra("MSG",AppConstant.PlayerMsg.PAUSE_MSG);//通知播放服务暂停播放
                    intent2.setPackage(getPackageName());
                    startService(intent2);
                    //修改UI
                    //显示播放图片 表示单击后可以播放 执行的暂停播放功能
                    playOrPauseBtn.setBackgroundResource(R.drawable.play_selector);
                    isPlaying=true;
                    isPause = false;//暂停按钮隐藏
                    break;
                case TelephonyManager.CALL_STATE_IDLE: // 挂机状态

                    //挂机状态  显示暂停按钮  表示单击后会暂停   这时候 执行的是继续播放功能
                    Intent intent = new Intent(ShowContentActivity.this,MyPlayService.class);
                    intent.setAction("com.xingyun.media.MUSIC_SERVICE");
                    intent.putExtra("MSG",AppConstant.PlayerMsg.CONTINUE_MSG);//继续播放音乐
                    intent.setPackage(getPackageName());
                    startService(intent);

                    //更新UI
                    //显示暂停按钮 单击可执行暂停功能  现在执行播放功能
                    playOrPauseBtn.setBackgroundResource(R.drawable.pause_selector);
                    isPlaying = false;//播放图片被隐藏
                    isPause = true;//显示暂停图片   执行播放功能
                    break;
                default:
                    break;

            }
        }
    }
    /**  回调音量控制函数  */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:	//按音量减键
                if(action == KeyEvent.ACTION_UP) {
                    if(currentVolume < maxVolume) {
                        currentVolume = currentVolume + 1;
                        sb_player_voice.setProgress(currentVolume);
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    } else {
                        am.setStreamVolume(AudioManager.STREAM_MUSIC,
                                currentVolume, 0);
                    }
                }
                return false;
            case KeyEvent.KEYCODE_VOLUME_DOWN:	//按音量加键
                if(action == KeyEvent.ACTION_UP) {
                    if(currentVolume > 0) {
                        currentVolume = currentVolume - 1;
                        sb_player_voice.setProgress(currentVolume);
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    } else {
                        am.setStreamVolume(AudioManager.STREAM_MUSIC,
                                currentVolume, 0);
                    }
                }
                return false;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
    /**  控制显示音量控制面板的动画 */
    public void voicePanelAnimation() {
        if(ll_player_voice.getVisibility() == View.GONE) {
            ll_player_voice.startAnimation(showVoicePanelAnimation);
            ll_player_voice.setVisibility(View.VISIBLE);
        }
        else{
            ll_player_voice.startAnimation(hiddenVoicePanelAnimation);
            ll_player_voice.setVisibility(View.GONE);
        }
    }
    /*******播放界面结束 ***********************************************************************************/
}
