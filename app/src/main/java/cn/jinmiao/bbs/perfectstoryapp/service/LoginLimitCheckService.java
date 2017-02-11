package cn.jinmiao.bbs.perfectstoryapp.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.Date;
import java.util.Random;

import cn.jinmiao.bbs.perfectstoryapp.bean.LoginLimitResultBeans;
import cn.jinmiao.bbs.perfectstoryapp.main.MainActivity;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyHttpCallbackListenerUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyPostHttpUtil;

public class LoginLimitCheckService extends Service {

    private String checkOnlineUserResultJSON;

    public LoginLimitCheckService() {
    }

    //服务第一次被创建时调用
    @Override
    public void onCreate() {
        super.onCreate();
     //   Log.v("FairyDebug2", "--------检测服务被第一次创建--------");
//
    }

    //每次服务启动的时候调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //这里开启线程进行检测 具体逻辑
              //  Log.v("FairyDebug2", "------service-------5秒后----代码检测一次");
                CheckLoginMethod();
            }
        }).start();

        //Alarm机制  每隔一个小时去执行一次CheckLimitLoginAlarmReceiver里面的onReceive()方法
        //获取一个 AlarmManager 的实例
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int fiveSecondTime = 5000; // 每隔5秒调用一次服务
        long triggerAtTime = SystemClock.elapsedRealtime() + fiveSecondTime;
        Intent i = new Intent(this, CheckLimitLoginAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    private void CheckLoginMethod() {

        SharedPreferences pref = LoginLimitCheckService.this.getSharedPreferences("TempUserUIdFile", LoginLimitCheckService.this.MODE_PRIVATE);
        int userUid = pref.getInt("tempUserUid", -1);

        if (userUid == -1) {
            stopSelf();
            return ;
        }

        //how to build  OnlySign
//        TelephonyManager tm = (TelephonyManager) LoginLimitCheckService.this.getSystemService(Context.TELEPHONY_SERVICE);
//        String DEVICE_ID =tm.getDeviceId()+"android_mobile";

        WifiManager wm = (WifiManager)LoginLimitCheckService.this.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        String DEVICE_ID =m_szWLANMAC+"android_mobile";
        MyPostHttpUtil.myPostHttpRequestUtils("http://122.114.58.68:8066/AppService/UserService.ashx?action=checkOnline", "&Uid=" + userUid + "&OnlySign=" + DEVICE_ID, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                checkOnlineUserResultJSON = response;
                checkOnlineUserDeviceHandler.sendEmptyMessage(1);
            }

            @Override
            public void onError(Exception e) {
                checkOnlineUserDeviceHandler.sendEmptyMessage(2);
            }
        });
    }

    private Handler checkOnlineUserDeviceHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    LoginLimitResultBeans loginLimitResultBeans = null;
                    try {
                        Gson checkOnlineUserGson = new Gson();
                        loginLimitResultBeans = checkOnlineUserGson.fromJson(checkOnlineUserResultJSON, LoginLimitResultBeans.class);
                    } catch (JsonSyntaxException e) {
                        //Toast.makeText(LoginLimitCheckService.this, "解析异常", Toast.LENGTH_LONG).show();
                        checkOnlineUserDeviceHandler.sendEmptyMessage(2);
                        break;
                    }

                    if(loginLimitResultBeans==null)
                    {
                        checkOnlineUserDeviceHandler.sendEmptyMessage(2);
                        break;
                    }

                  //  Log.v("FairyDebug2", "----------在线检测结果---------------" +loginLimitResultBeans.getLoginLimit());
                    if (loginLimitResultBeans.getLoginLimit().trim().equals("on")) {

                     //   Log.v("FairyDebug2", "------------------------------" + "您被强制下线");
                        Intent intent = new Intent("com.xingyun.broadcastbestpractice.FORCE_OFFLINE");
                        sendOrderedBroadcast(intent, null);

                    } else if (loginLimitResultBeans.getLoginLimit().trim().equals("off")) {
                        break;
                    }
                    break;
                case 2:
                    Log.v("FairyDebug2"," case 2 some issue");
                    break;
                default:
                    break;
            }
        }
    };




    //服务销毁的时候
    @Override
    public void onDestroy() {
        super.onDestroy();
      //  Log.v("FairyDebug2", "检测服务被销毁");
       // unregisterReceiver(forceOffLineReceiver);
    }

    //服务必须要实现的抽象方法
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
