package cn.jinmiao.bbs.perfectstoryapp.main;

import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.fragment.MyHomeFragment;
import cn.jinmiao.bbs.perfectstoryapp.fragment.home.HomeFragment;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.UserCenterFragment;
import cn.jinmiao.bbs.perfectstoryapp.service.LoginLimitCheckService;
import cn.jinmiao.bbs.perfectstoryapp.service.MyPlayService;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppConstant;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    //底部两个界面  故事 和 登录
    private HomeFragment homeFragment;//默认主页
    private UserCenterFragment userCenterFragment;//用户中心

    private MyHomeFragment myHomeFragment;

    //故事和 登录 按钮
    private TextView homeStory_TextView;
    private TextView userCenter_TextView;

    private Drawable homeStoryDrawable;//故事图片
    private Drawable userCenterDrawable;//我

    //Fragment管理器
    private FragmentManager fragmentManager;
    // Fragment事务管理器
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  Log.v("FairyDebug", "MainActivity ---------OnCreate() 被调用");
        AppCmpaActivityCollector.addAppCompatActivity(this);
        //加载布局
        setContentView(R.layout.activity_main);

        // 初始化界面找Id
        initView();

        //获取Fragment 管理器
        fragmentManager = getFragmentManager();

        setTabSelection(0);//设置第一个显示的Fragment界面

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // Log.v("FairyDebug", "MainActivity ---------OnDestroy() 被调用");
        AppCmpaActivityCollector.removeAppCompatActivity(this);
    }

    /**
     * 初始化界面找Id
     */
    private void initView() {
        //默认首页故事按钮
        homeStory_TextView = (TextView) findViewById(R.id.tab_bottom_home_story_id);
        homeStory_TextView.setOnClickListener(this);
        //默认首页用户中心按钮
        userCenter_TextView = (TextView) findViewById(R.id.tab_bottom_userCenter_id);
        userCenter_TextView.setOnClickListener(this);
    }


    /**
     * 强制停止播放的方法
     * */
    public void stopPlayMusic() {
        //启动停止播放服务
        Intent intent2 = new Intent(MainActivity.this,MyPlayService.class);
        intent2.setAction("com.xingyun.media.MUSIC_SERVICE");//这个参数在清单文件中
        intent2.setPackage(getPackageName());
        stopService(intent2);

        //停止检测
        Intent checkOnlineService=new Intent(MainActivity.this,LoginLimitCheckService.class);
        stopService(checkOnlineService);
    }

    /**
     * 返回键
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 键盘码如果是返回键 则返回上一页面
        if(keyCode==KeyEvent.KEYCODE_BACK){
            ExitDialog();//弹出退出对话框
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_bottom_home_story_id:
                setTabSelection(0);
                break;
            case R.id.tab_bottom_userCenter_id:
                setTabSelection(1);
                break;
            default:
                break;
        }
    }

    private void setTabSelection(int index) {

        //控件重置
        resetBtnMethod();
        //开启事务管理器
        fragmentTransaction = fragmentManager.beginTransaction();

        //隐藏所有事务,避免重叠
        MyHideFragment(fragmentTransaction);

        switch (index)
        {
            case 0:
                //如果版本高于21
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    homeStoryDrawable=this.getResources().getDrawable(R.drawable.bottom_story_press,getTheme());
                }else if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                    homeStoryDrawable=this.getResources().getDrawable(R.drawable.bottom_story_press);
                }
                homeStoryDrawable.setBounds(0, 0, homeStoryDrawable.getMinimumWidth(), homeStoryDrawable.getMinimumHeight());
                homeStory_TextView.setCompoundDrawables(null, homeStoryDrawable, null, null);

                if(homeFragment==null)
                {
                    //homeFragment=new HomeFragment();
                    //// TODO: 2016/12/14 0014
                    myHomeFragment = new MyHomeFragment();
                    fragmentTransaction.add(R.id.tab_Content_id,myHomeFragment);
                } else{
                    fragmentTransaction.show(myHomeFragment);
                }
                break;
            case 1:

                //如果版本高于21
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    userCenterDrawable=this.getResources().getDrawable(R.drawable.bottom_account_press,getTheme());
                }else if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                    userCenterDrawable=this.getResources().getDrawable(R.drawable.bottom_account_press);
                }
                userCenterDrawable.setBounds(0, 0, userCenterDrawable.getMinimumWidth(), userCenterDrawable.getMinimumHeight());
                userCenter_TextView.setCompoundDrawables(null, userCenterDrawable, null, null);
                if(userCenterFragment==null)
                {
                    userCenterFragment=new UserCenterFragment();
                    fragmentTransaction.add(R.id.tab_Content_id,userCenterFragment);
                    fragmentTransaction.hide(myHomeFragment);
                } else{
                    fragmentTransaction.show(userCenterFragment);
                    fragmentTransaction.hide(myHomeFragment);
                }
                break;
            default:
                break;
        }
        //提交事务
        fragmentTransaction.commit();
    }

    private void MyHideFragment(FragmentTransaction fragmentTransaction) {
        if(myHomeFragment!=null)
        {
            fragmentTransaction.hide(myHomeFragment);
        }

        if(userCenterFragment!=null)
        {
            fragmentTransaction.hide(userCenterFragment);
        }
    }


    private void resetBtnMethod() {

        //如果版本高于21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            homeStoryDrawable=this.getResources().getDrawable(R.drawable.bottom_story_normal,getTheme());
        }else if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            homeStoryDrawable=getResources().getDrawable(R.drawable.bottom_story_normal);
        }

        homeStoryDrawable.setBounds(0, 0, homeStoryDrawable.getMinimumWidth(), homeStoryDrawable.getMinimumHeight());
        homeStory_TextView.setCompoundDrawables(null, homeStoryDrawable, null, null);

        //如果版本高于21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            userCenterDrawable=this.getResources().getDrawable(R.drawable.bottom_account_normal,getTheme());
        }else if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            userCenterDrawable=getResources().getDrawable(R.drawable.bottom_account_normal);
        }
        userCenterDrawable.setBounds(0,0,userCenterDrawable.getMinimumWidth(),userCenterDrawable.getMinimumHeight());
        userCenter_TextView.setCompoundDrawables(null,userCenterDrawable,null,null);

    }

    private void ExitDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("您确定想要退出么?");
        builder.setTitle("信息");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                    try {
                        stopPlayMusic();//尝试停止播放服务
                    } catch (Exception e) {
                        Log.v("FairyDebug", "不需要暂停");
                    }
                //当finish()方法被调用时结束该活动
                AppCmpaActivityCollector.finishAll();
                ExitMethod();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void ExitMethod() {

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);

            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(getPackageName());
        }
    }
}
