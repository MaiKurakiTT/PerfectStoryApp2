package cn.jinmiao.bbs.perfectstoryapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;

import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;

/**
 * Created by zhaqingf on 2016/3/18.
 */
public class ForceOfflineReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        Toast.makeText(context, "您的账号在另一台设备登陆,即将强制被迫下线,如果不是您操作，请尽快重新登陆修改密码", Toast.LENGTH_LONG).show();

        //延迟后跳转页面MainActivity 页面
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ForceExitMethod(context);
            }
        }, 3000);

        abortBroadcast();

    }

    private void ForceExitMethod(Context context) {

        // 注销登陆
        ClearData(context);// 清除登录数据
        try {
            //启动停止播放服务
            Intent intent2 = new Intent(context, MyPlayService.class);
            intent2.setAction("com.xingyun.media.MUSIC_SERVICE");//这个参数在清单文件中
            intent2.setPackage(context.getPackageName());
            context.stopService(intent2);
        } catch (Exception e) {
            //Log.v("FairyDebug", "不需要暂停");
        }

        //结束所有活动
        AppCmpaActivityCollector.finishAll();
        ExitMethod(context);
    }


    private void ExitMethod(Context context) {

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
            System.exit(0);
        }
    }

    private void ClearData(Context context) {

        //用户ID初始化
        SharedPreferences.Editor editor =context.getSharedPreferences("TempUserUIdFile", context.MODE_PRIVATE).edit();
        editor.putInt("tempUserUid", -1);
        editor.commit();

        File file = new File("/data/data/" + context.getPackageName().toString() + "/shared_prefs", "LoginUserShareference.xml");

        if (file.exists()) {
            SharedPreferences sharedata = context.getSharedPreferences("LoginUserShareference",context.MODE_PRIVATE);
            sharedata.edit().clear().commit();
            file.delete();

        }
    }

}
