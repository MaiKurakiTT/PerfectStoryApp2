package cn.jinmiao.bbs.perfectstoryapp.main;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.io.File;
import java.io.IOException;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.db.HistoryPlayDataBase;
import cn.jinmiao.bbs.perfectstoryapp.service.LoginLimitCheckService;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;

/***
 * Splash启动动画
 * */
public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGHT=1500;//设置图片显示延迟3秒
    private HistoryPlayDataBase historyPlayDataBase;//
    private static final String BASE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "jmpfa" + File.separator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Log.v("FairyDebug", "SplashActivity-----------Splash动画-------OnCreate");
        setContentView(R.layout.activity_splash);
        x.Ext.init(getApplication());
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        File file = new File(BASE_PATH);
        boolean b = file.exists();
        if(!file.exists()){
            file.mkdir();
        }

        //创建历史记录数据库
        historyPlayDataBase=new HistoryPlayDataBase(SplashActivity.this,"HistoryMusic.db", null,1);
        historyPlayDataBase.getWritableDatabase();
        historyPlayDataBase.close();

        //延迟后跳转页面MainActivity 页面
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                //创建跳转页面意图对象
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                //启动意图
                startActivity(intent);
                //结束当前活动
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.v("FairyDebug", "SplashActivity-----------Splash动画-------onDestroy");
    }

}
