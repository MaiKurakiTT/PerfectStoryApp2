package cn.jinmiao.bbs.perfectstoryapp.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;
import cn.jinmiao.bbs.perfectstoryapp.bean.UserInfo;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.register.RegisterPageActivity;
import cn.jinmiao.bbs.perfectstoryapp.main.ShowDialogActivity;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppConstant;
import cn.jinmiao.bbs.perfectstoryapp.utils.SharePreferenceObjectTools;

/**
 * Created by fairy on 2016/1/23.
 */
public class MyPlayService extends Service implements OnCompletionListener, OnPreparedListener {


    public MediaPlayer mediaPlayer;//媒体播放器

    private int status = 2;    //播放状态，默认为全部循环播放
    private int current;// 记录当前正在播放的音乐列表位置
    private String url; // 音乐文件路径
    private int currentTime; //当前播放时长
    private int duration; //当前音乐播放总时长
    private int msg;//播放信息
    private boolean isPause; // 暂停状态


    private List<MP3Info> mp3InfoList;//当前播放队列

    private MyReceivePlayMode myReceiverPalyMode;    //自定义广播接收器  监听循环模式改变

    private Integer currentPlayMusicId = null;//当前播放Id

    private Intent CurrentIntent;
    //服务要发送的一些Action
    public static final String UPDATE_ACTION = "com.xingyun.action.UPDATE_ACTION"; // 更新动作
    public static final String CTL_ACTION = "com.xingyun.action.CTL_ACTION"; // 控制动作
    public static final String MUSIC_CURRENT = "com.xingyun.action.MUSIC_CURRENT"; // 音乐当前时间改变动作
    public static final String MUSIC_DURATION = "com.xingyun.action.MUSIC_DURATION";// 音乐播放长度改变动作
    public static final String MUSIC_PLAYING = "com.xingyun.action.MUSIC_PLAYING"; // 音乐正在播放动作
    public static final String REPEAT_ACTION = "com.xingyun.action.REPEAT_ACTION"; // 音乐重复播放动作
    public static final String SHUFFLE_ACTION = "com.xingyun.action.SHUFFLE_ACTION";// 音乐随机播放动作

    /**
     * 服务第一次被创建时调用 初始化播放器设置
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("FairyDebug", "服务第一次被创建");

        //初始化一个音乐播放器对象
        mediaPlayer = new MediaPlayer(); //创建一个媒体播放器对象
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
        // mediaPlayer.setOnBufferingUpdateListener(this);//设置网络缓存数据缓冲更新监听
        mediaPlayer.setOnCompletionListener(this);// 实现一个OnCompletionListener接口 当前歌曲播放完成时设置监听
        mediaPlayer.setOnPreparedListener(this);

        //自定义广播接收器
        // 通过接受自定义的广播接收器控制播放模式  单曲循环 全部循环 随机播放
        myReceiverPalyMode = new MyReceivePlayMode();
        IntentFilter filter = new IntentFilter();//过滤器
        filter.addAction(CTL_ACTION);// 控制播放模式动作
        registerReceiver(myReceiverPalyMode, filter);//自定义接收器 接受过滤的广播

    }
    //创建一个广播接收器  接受播放模式改变 执行结果： status =2
    public class MyReceivePlayMode extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            switch (control) {
                case 1:
                    status = 1; // 将播放状态置为1表示：单曲循环
                    break;
                case 2:
                    status = 2;    //将播放状态置为2表示：全部循环
                    break;
                case 3:
                    status = 3;    //将播放状态置为3表示：顺序播放
                    break;
                case 4:
                    status = 4;    //将播放状态置为4表示：随机播放
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 服务每次被创建时调用
     * <p/>
     * 调用时需要传值
     * url: 歌曲播放路径
     * listPosition :当前歌曲在播放队列中的位置
     * MSG : 播放状态
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

      //  Log.v("FairyDebug", "服务被调用一次");

        ExecutorService singleThreadExecutor2 = Executors.newSingleThreadExecutor();
        singleThreadExecutor2.execute(new Runnable() {
            @Override
            public void run() {
                CurrentIntent=intent;
                url = intent.getStringExtra("url");
                dealWithPlayMethodHandler.sendEmptyMessage(1);
            }
        });


       // Log.v("FairyDebug", "MyPlayService -------------------------------服务被调用一次结束-----------------------------" );
        return super.onStartCommand(intent, flags, startId);
    }

    private Handler dealWithPlayMethodHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 1:
                   executePlayServiceMethod();
                   break;
               default:
                   break;
           }
        }
    };

    private void executePlayServiceMethod(){
        //获取启动服务接受到的数据
        msg = CurrentIntent.getIntExtra("MSG", -1);  //播放信息
        //Log.v("FairyDebug", "MyPlayService -------onStartCommand----------服务被调用接受到的所需服务类型" + msg);

        //url = CurrentIntent.getStringExtra("url"); //歌曲路径
        if(url!=null)
        {
            current = CurrentIntent.getIntExtra("listPosition", -1);//当前播放歌曲在播放列表的位置
//            Log.v("FairyDebug", "MyPlayService -------------------------------服务被调用一次-----------------------------" );
//            Log.v("FairyDebug", "MyPlayService ------onStartCommand-----------服务被调用接受到的歌曲播放地址" + url);
//            Log.v("FairyDebug", "MyPlayService ------onStartCommand-----------服务被调用接受到的歌曲播放位置" + current);

        }

        if (msg == AppConstant.PlayerMsg.PLAY_MSG) {    //直接播放音乐
           // Log.v("FairyDebug", "MyPlayService ----onStartCommand-------直接播放音乐-----MSG=PLAY_MSG------" + msg);
            play(0,url);// 0表示从头开始播放 url播放歌曲路径
        } else if (msg == AppConstant.PlayerMsg.PAUSE_MSG) {    //暂停
            pause();
        } else if (msg == AppConstant.PlayerMsg.STOP_MSG) {        //停止
            stop();
        } else if (msg == AppConstant.PlayerMsg.CONTINUE_MSG) {    //继续播放
            resume();
        } else if (msg == AppConstant.PlayerMsg.PRIVIOUS_MSG) {    //上一首;
            previous(url);
        } else if (msg == AppConstant.PlayerMsg.NEXT_MSG) {        //下一首
            next(url);
        } else if (msg == AppConstant.PlayerMsg.PROGRESS_CHANGE) {    //进度更新 手动滑动
            currentTime = CurrentIntent.getIntExtra("progress", -1);
           // url = CurrentIntent.getStringExtra("url");
            play(currentTime, url);
        } else if (msg == AppConstant.PlayerMsg.PLAYING_MSG) {//正在播放
            updateMusicTimeHandler.sendEmptyMessage(1);
        }

    }

    private static final String BASE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator +"jmpfa" + File.separator;
    /**
     * 开始播放
     */
    private void play(int currentTime, String url) {
       // Log.v("FairyDebug", "MyPlayService --------play(int currentTime,String url)---- has been executed ---------" + msg);
        //播放网络歌曲
//        Log.v("FairyDebug", "MyPlayService --------当前歌曲播放路径：" + url);
//        Log.v("FairyDebug", "MyPlayService --------当前歌曲播放时间：" + currentTime);
        // http://mp3.jinmiao.cn/mp3file/01tonghua/11/acndxhg0np.mp3
        String path = url.replace("http://mp3.jinmiao.cn/mp3file/","").replace("mp3","psa").replace("/","");
        path = BASE_PATH + path;
        File file = new File(path);
        mediaPlayer.reset();// 把各项参数恢复到初始状态 控件自带的方法
        boolean b = file.exists();
        if(b){
            try {
                Log.i("mediaPlayer","本地存在文件,播放本地歌曲");
                mediaPlayer.setDataSource(path);//播放当前地址歌曲
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                Log.i("mediaPlayer","本地不存在文件,播放网络歌曲");
                mediaPlayer.setDataSource(url);//播放当前地址歌曲
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            mediaPlayer.prepare(); //进行缓冲
            mediaPlayer.setOnPreparedListener(new MyPreparedListener(currentTime,url));//注册一个监听器 监听缓冲状态 缓冲可播放时执行播放
            //Handle 更新UI
            updateMusicTimeHandler.sendEmptyMessage(1);
        } catch (IOException e) {
            Log.v("FairyDebug", "MyPlayService -------服务播放IO异常");
        } catch (IllegalArgumentException e) {
            Log.v("FairyDebug", "MyPlayService -------服务数据格式异常");
        } catch (SecurityException e) {
            Log.v("FairyDebug", "MyPlayService -------服务系统安全异常");
        } catch (IllegalStateException e) {
            Log.v("FairyDebug", "MyPlayService ------栈溢出异常");
        }
    }


    /**
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
     */
    private final class MyPreparedListener implements OnPreparedListener {
        private int currentTime;//缓冲当前播放进度
        private String url;

        public MyPreparedListener(int currentTime,String url) {
            this.currentTime = currentTime;
            this.url=url;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
           // Log.v("FairyDebug", "MyPlayService ----------------音乐准备完毕,现在可以播放了");
            if (currentTime > 0) { // 如果音乐不是从头播放
                mediaPlayer.seekTo(currentTime);
            }
            mediaPlayer.start(); // 开始播放
            //读取完毕告诉播放界面当前歌曲播放的长度时间  传递duration
            Intent intent = new Intent();
            intent.setAction(MUSIC_DURATION);//音乐播放长度改变动作
            duration = mediaPlayer.getDuration();//获取音频的时长
            intent.putExtra("duration", duration); //通过Intent来传递歌曲的总时长
            sendBroadcast(intent);// 给 PlayMusicListActivity 发送广播
        }
    }



    /**
     * 设置音乐播放完成时的监听器
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        /**
         * 音乐播放完毕执行播放模式
         * status==1 单曲循环
         * status==2 全部循环
         * status==3 随机播放
         * */
        if (status == 1)// 单曲循环
        {
            mediaPlayer.start();
        }else if (status == 2) {// 全部循环
            //获取当前播放队列列表  因为播放列表可能被改变
            mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(MyPlayService.this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");
            if(mp3InfoList==null)//如果播放列表等于空 则直接退出
            {
                Toast.makeText(MyPlayService.this,"当前播放列表为空无法播放歌曲,请返回选择要播放的歌曲",Toast.LENGTH_SHORT).show();
                return ;
            }
            current++;//当前位置移动到下一首
            if (current > mp3InfoList.size() - 1) {//如果大于播放列表长度,那么变为第一首的位置继续播放
                current = 0;//当前播放队列最后一个播放完毕,那么跳转到播放列表第一首歌曲进行播放
            }
            // 更新当前播放位置
            SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
            editor.putInt("currentPlayMusicId", Integer.valueOf(current));//当前播放专辑Id  这个Id可以获取当前播放列表
            editor.commit();
            //发送广播 更新播放歌曲
            Intent sendIntent = new Intent(UPDATE_ACTION);// 更新当前播放歌曲动作
            sendIntent.putExtra("current",current);//当前正在播放的歌曲位置
            // 执行发送广播，将被Activity组件中的BroadcastReceiver接收到
            sendBroadcast(sendIntent);

            //构造播放路径
            url = getString(R.string.MP3PlayHomeAddress) + "/" + mp3InfoList.get(current).getMp3_path();
          //  Log.v("FairyDebug", "全部循环播放路径测试" + url);
            //开始播放 从头开始播放
            play(0, url);// play(int current time,String url)
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    /**
     * handler用来接收消息，来发送广播更新播放时间
     */
    private Handler updateMusicTimeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (mediaPlayer != null) {
                    currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置

                    Intent intent = new Intent();
                    intent.setAction(MUSIC_CURRENT);// 音乐当前时间改变动作
                    intent.putExtra("currentTime", currentTime);//当前播放进度
                    sendBroadcast(intent); // 给PlayMusicListActivity 发送广播
                    if(currentTime<getResources().getInteger(R.integer.time))
                    {
                        updateMusicTimeHandler.sendEmptyMessageDelayed(1, 1000);//延迟发送消息 播放时间一秒更新一次
                    }
                    //权限设置 如果是会员则正常播放，否则启动试听模式
                    if (currentTime >= getResources().getInteger(R.integer.time)) {//当播放进度大于十秒后执行下面代码
                        //读取本地登录用户数据 检查权限
                        UserInfo mCurrentLoginUser = (UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(MyPlayService.this, "LoginUserShareference", "LoginUserShareferenceKey");
                        //如果无登陆即mCurrentLoginUser=null 登录无权限 mCurrentLoginUser.getVgid().equals("0")

                        SharedPreferences pref = MyPlayService.this.getSharedPreferences("checkVIP_FIlE",MyPlayService.this.MODE_PRIVATE);
                        boolean   vipIsExpired= pref.getBoolean("VIP_IS_EXPIRED",false);// 如果为false 执行禁止播放，否则正常播放
                        if (mCurrentLoginUser == null ||mCurrentLoginUser.getVgid()==null || (!vipIsExpired) ) {
                            forbiddenPlayStart();
                        }else{
                            updateMusicTimeHandler.sendEmptyMessageDelayed(1, 1000);//延迟发送消息 播放时间一秒更新一次
                        }
                    }
                }
            }
        }
    };



    private void forbiddenPlayStart() {

        /*Intent intent = new Intent(getApplicationContext(), ShowDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/

        android.app.AlertDialog.Builder alter = new AlertDialog.Builder(getApplicationContext());
        alter.setTitle("提示")
                .setMessage("好好听故事网提醒：\n" +
                        "\n" +
                        "一 教育投资要尽早，敢收费是最好的。\n" +
                        "\n" +
                        "二 播音员录制，十一五课题，2万音频。\n" +
                        "\n" +
                        "三 全部试听90秒，付费收听完整版。\n" +
                        "\n" +
                        "四 支持手机APP，年付192元，月付20元。\n" +
                        "\n" +
                        "五 让天下孩子都能有健康高尚人格。")
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getApplicationContext(),"攻城狮正在开发中,敬请期待...",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("成为VIP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Intent jumpRegisterIntent=new Intent(getApplicationContext(),RegisterPageActivity.class);
                        jumpRegisterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(jumpRegisterIntent);
                    }
                });
            AlertDialog dialog = alter.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(false); //点击外面区域不会让dialog消失
        dialog.show();


       // Toast.makeText(MyPlayService.this, "对不起，非会员只能试听"+getResources().getInteger(R.integer.time)/1000+"秒", Toast.LENGTH_LONG).show();

        stop();

        return ;
//        //读取本地文件获取当前歌曲播放位置
//        SharedPreferences pref = MyPlayService.this.getSharedPreferences("currentPlayAlbumDataFile", MyPlayService.this.MODE_PRIVATE);
//        currentPlayMusicId = pref.getInt("currentPlayMusicId", -1);//当前播放的是第几首歌
//        currentPlayMusicId = currentPlayMusicId + 1;//进入下一曲歌曲位置
//        //获取当前播放列表对象
//        mp3InfoList = (List<MP3Info>) SharePreferenceObjectTools.readSharePreferencesObject(MyPlayService.this, "PlayMusicListSharePreferenceFile", "PlayMusicListSharePreferenceKey");
//        //下一曲超出了列表大小从头开始播放
//        if (currentPlayMusicId > (mp3InfoList.size() - 1)) {
//            currentPlayMusicId = 0;
//        }
//        //更新当前位置
//        SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
//        editor.putInt("currentPlayMusicId", Integer.valueOf(currentPlayMusicId));//当前播放专辑Id  这个Id可以获取当前播放列表
//        editor.commit();
//        //获取新的播放地址
//        url = getString(R.string.MP3PlayHomeAddress) + "/" + mp3InfoList.get(currentPlayMusicId).getMp3_path();
//        Log.v("FairyDebug", "更新后播放URL" + url);
//        //启动音乐播放服务
//
//        Intent intent = new Intent();//启动音乐服务
//        intent.setAction("com.xingyun.media.MUSIC_SERVICE");
//        intent.putExtra("url", url);//获取当前音乐播放地址
//        intent.putExtra("listPosition", currentPlayMusicId);
//        intent.putExtra("MSG", AppConstant.PlayerMsg.NEXT_MSG);//通知服务播放歌曲
//        intent.setPackage(getPackageName());
//        startService(intent);
    }



    /**
     * 服务被销毁时调用
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
      //  Log.v("FairyDebug", "服务被销毁");
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 停止
     */
    public void stop() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                updateMusicTimeHandler.removeMessages(1);
            }
        } catch (IllegalStateException e) {
            //Log.v("FairyDebug","停止播放失败");
        }
    }

    /**
     * 继续播放
     */
    private void resume() {
        if (isPause) {
            mediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 上一首
     */
    private void previous(String url) {
        Intent sendIntent = new Intent(UPDATE_ACTION);
        sendIntent.putExtra("current", current);
        // 保存实现持久化操作
        SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
        editor.putInt("currentPlayMusicId", Integer.valueOf(current));//当前播放专辑Id  这个Id可以获取当前播放列表
        editor.commit();
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
        sendBroadcast(sendIntent);
        play(0, url);
    }

    /**
     * 下一首
     */
    private void next(String url) {
        Intent sendIntent = new Intent(UPDATE_ACTION);
        sendIntent.putExtra("current", current);
        // 保存实现持久化操作
        SharedPreferences.Editor editor = getSharedPreferences("currentPlayAlbumDataFile", MODE_PRIVATE).edit();
        editor.putInt("currentPlayMusicId", Integer.valueOf(current));//当前播放专辑Id  这个Id可以获取当前播放列表
        editor.commit();
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
        sendBroadcast(sendIntent);
        play(0, url);
    }

    /**
     * 继承服务必须实现的抽象方法
     * 任何服务必须实现的一个抽象方法
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
