package cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.LoginJSONData;
import cn.jinmiao.bbs.perfectstoryapp.bean.LoginLimitResultBeans;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;
import cn.jinmiao.bbs.perfectstoryapp.bean.UserInfo;
import cn.jinmiao.bbs.perfectstoryapp.fragment.home.HomeFragment;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.aboutus.ContactUsActivity;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.history.HistoryPlayListActivity;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.modifypwd.ModifyPasswordActivity;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.recharge.RechargeActivity;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.register.RegisterPageActivity;
import cn.jinmiao.bbs.perfectstoryapp.main.DownloadListActivity;
import cn.jinmiao.bbs.perfectstoryapp.main.MainActivity;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.collection.PlayCollectionListActivity;
import cn.jinmiao.bbs.perfectstoryapp.main.SplashActivity;
import cn.jinmiao.bbs.perfectstoryapp.service.LoginLimitCheckService;
import cn.jinmiao.bbs.perfectstoryapp.service.MyPlayService;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppConstant;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyGetHttpUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyHttpCallbackListenerUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyPostHttpUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.SharePreferenceObjectTools;


public class UserCenterFragment extends Fragment implements View.OnClickListener{
    private View userCenterView;
    private TextView title_textView;
    private Button register_Btn;
    private Button login_Btn;

    private LinearLayout downloadView;

    private EditText accout_Login_EditText;
    private EditText password_Login_EditText;

    private Button exit_Logout_LoginBtn;

    private  String accout;
    private String password;
    private String LoginReturnJSONData;
    private String LoginURL;

    private String loginUserJSONData;

    private String loginLimitResultJSON;

    //登录后
    private TextView loginUserNameTextView;
    private TextView loginUserInfoTextView;

    private  UserInfo mCurrentLoginUser;//当前登录用户
    private  Integer  tempUserUid;

    private String myCollectionListJSONData;//我的收藏JSON数据

    private List<LoginJSONData> loginJSONDataList=new ArrayList<LoginJSONData>();


    private List<MP3Info> myCollectionList=new ArrayList<MP3Info>();

    private Context mContext;

    private ProgressDialog progressDialog;// 加载进度框

    @Override
    public void onAttach(Context context) {
        this.mContext=context;
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        this.mContext=activity.getApplicationContext();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCurrentLoginUser=(UserInfo)SharePreferenceObjectTools.readSharePreferencesObject(mContext, "LoginUserShareference", "LoginUserShareferenceKey");

        // 未登录布局
        if(mCurrentLoginUser==null)
        {
            userCenterView=inflater.inflate(R.layout.fragment_user_center, container, false);
            findViewById();
            title_textView.setText("登录好好听故事网");
        }else if(mCurrentLoginUser!=null)// 用户登录后布局
        {
            userCenterView = inflater.inflate(R.layout.activity_after_login, container, false);

            LinearLayout modifyPwdLayout=(LinearLayout)userCenterView.findViewById(R.id.modify_password_layout);
            modifyPwdLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent modifyPwdIntent=new Intent(mContext, ModifyPasswordActivity.class);
                    startActivity(modifyPwdIntent);
                }
            });
            downloadView = (LinearLayout) userCenterView.findViewById(R.id.my_collection_download);
            downloadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DownloadListActivity.class);
                    startActivity(intent);
                }
            });


            LinearLayout ContactUSLayout=(LinearLayout)userCenterView.findViewById(R.id.contact_us_layout);
            LinearLayout myPlayHistoryBtn = (LinearLayout) userCenterView.findViewById(R.id.my_play_history);
            myPlayHistoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent playHistroyIntent=new Intent(mContext, HistoryPlayListActivity.class);
                    startActivity(playHistroyIntent);
                }
            });

            LinearLayout myShouCangMusicBtn = (LinearLayout) userCenterView.findViewById(R.id.my_collection_music);
            myShouCangMusicBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetCollectionListMethod();
                }
            });

            TextView Recharge_TextView=(TextView)userCenterView.findViewById(R.id.Recharge_TextView_id);
            Recharge_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent rechargeIntent=new Intent(mContext, RechargeActivity.class);
                    startActivity(rechargeIntent);
                }
            });

            ContactUSLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent contactUsIntent=new Intent(mContext, ContactUsActivity.class);
                    startActivity(contactUsIntent);
                }
            });

            ImageView mark_vip_imgview = (ImageView) userCenterView.findViewById(R.id.mark_vip_imgview_id);
            TextView register_time_TextView = (TextView) userCenterView.findViewById(R.id.register_time_TextView_id);
            TextView expire_time_TextView = (TextView) userCenterView.findViewById(R.id.expire_time_TextView_Id);
            mark_vip_imgview.setVisibility(View.GONE);
            register_time_TextView.setVisibility(View.GONE);
            expire_time_TextView.setVisibility(View.GONE);

            if (mCurrentLoginUser.getVgid()!=null) {
                mark_vip_imgview.setVisibility(View.VISIBLE);//显示VIP 图标
                register_time_TextView.setVisibility(View.VISIBLE);
                expire_time_TextView.setVisibility(View.VISIBLE);//显示到期时间

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date(Long.valueOf(mCurrentLoginUser.getJointime())*1000));
                register_time_TextView.setText("注册时间: " +date);


                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                String date2 = sdf2.format(new Date(Long.valueOf(mCurrentLoginUser.getExptime())*1000));
                expire_time_TextView.setText("到期时间: " + date2);

            }

            initLoginDataView(mCurrentLoginUser);
        }
        return userCenterView;
    }

    @Override
    public void onStart() {
        try{
        mContext=UserCenterFragment.this.getActivity();
        }catch (Exception e)
        {
            Log.e("FairyDebug","获取上下文对象失败");
        }
        super.onStart();
    }

    private void GetCollectionListMethod() {

        SharedPreferences pref = mContext.getSharedPreferences("TempUserUIdFile", Context.MODE_PRIVATE);
        int tempUserUid = pref.getInt("tempUserUid",-1);
        if(tempUserUid==-1)
        {
            Toast.makeText(mContext,"未知的异常-0x00",Toast.LENGTH_SHORT).show();
            return ;
        }
       // Log.v("FairyDebug","UserCenterFragment--------------------------当前登录用户ID="+tempUserUid);
        MyGetHttpUtil.mySendHttpRequestUtils(getString(R.string.getMusicListURL) + "?action=collectionList&UID=" + tempUserUid, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                myCollectionListJSONData = response;
                //   Log.v("FairyDebug", "UserCenterFragment--------------------------获取收藏列表JSON数据" + response);
                updateMyCollection.sendEmptyMessage(11);
            }

            @Override
            public void onError(Exception e) {
                updateMyCollection.sendEmptyMessage(12);
            }
        });
    }

    private Handler updateMyCollection=new Handler(){

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case 11:
                   Intent myCollectionIntent=new Intent(mContext, PlayCollectionListActivity.class);
                   myCollectionIntent.putExtra("myCollectJSONList",myCollectionListJSONData);
                   startActivity(myCollectionIntent);
                   break;
               case 12:
                   break;
               default:
                   break;
           }
        }
    };

    private void initLoginDataView(UserInfo userInfo) {

        //显示登陆的用户名
        loginUserNameTextView=(TextView)userCenterView.findViewById(R.id.login_userName_id);
        loginUserNameTextView.setText("" + userInfo.getUsername());



        //退出登录按钮
        exit_Logout_LoginBtn=(Button)userCenterView.findViewById(R.id.logout_accout_Btn);
        exit_Logout_LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearData();// 清除登录数据
                // 执行页面跳转
                Intent logoutIntent = new Intent(mContext, MainActivity.class);
                startActivity(logoutIntent);
                //结束当前界面
                UserCenterFragment.this.getActivity().finish();
            }
        });
    }

    private void ClearData() {
        File file = new File("/data/data/"
                +getActivity().getPackageName().toString() + "/shared_prefs",
                "LoginUserShareference.xml");

        if (file.exists()) {

            SharedPreferences sharedata = UserCenterFragment.this.getActivity().getApplication().getSharedPreferences("LoginUserShareference", Context.MODE_PRIVATE);
            sharedata.edit().clear().commit();
            file.delete();
            Toast.makeText(getActivity(), "注销成功", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void findViewById() {
        title_textView=(TextView)userCenterView.findViewById(R.id.bar_top_title);
        register_Btn=(Button)userCenterView.findViewById(R.id.register_User_Btn);
        register_Btn.setOnClickListener(this);
        login_Btn=(Button)userCenterView.findViewById(R.id.login_Btn_id);
        login_Btn.setOnClickListener(this);

        accout_Login_EditText=(EditText)userCenterView.findViewById(R.id.user_account_id);
        password_Login_EditText=(EditText)userCenterView.findViewById(R.id.user_password_id);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.register_User_Btn:
                Intent jumpRegisterIntent=new Intent(mContext,RegisterPageActivity.class);
                startActivity(jumpRegisterIntent);
                break;
            case R.id.login_Btn_id:
                if(accout_Login_EditText.getText().toString().trim().equals(""))
                {
                    Toast.makeText(mContext,"账号不可为空",Toast.LENGTH_SHORT).show();
                    return ;
                }else if(password_Login_EditText.getText().toString().trim().equals("")){
                    Toast.makeText(mContext, "密码不可为空", Toast.LENGTH_SHORT).show();
                    return ;
                }
                progressDialog = ProgressDialog.show(mContext, "请稍等...", "正在登录...", true);
                LoginMethod();

                break;
            default:
                break;
        }
    }

    private void LoginMethod() {

        LoginURL=getResources().getString(R.string.LoginURL)+"?action=doLogin";
        accout=accout_Login_EditText.getText().toString().trim();
        password=password_Login_EditText.getText().toString().trim();

        try {
            accout = URLEncoder.encode(accout, "UTF-8");
            password = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
           //Log.v("FairyDebug","URL转码失败");
        }

        MyPostHttpUtil.myPostHttpRequestUtils(LoginURL, "&Account=" + accout + "&Password=" + password, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                //Log.v("FairyDebug", "登录返回JSON数据结果" + response);
                LoginReturnJSONData = response;
                LoginUserHandle.sendEmptyMessage(1);
            }

            @Override
            public void onError(Exception e) {
                LoginUserHandle.sendEmptyMessage(2);
            }
        });
    }

    private Handler LoginUserHandle=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                        try {
                            Gson gson=new Gson();
                            LoginJSONData mLoginJSONData= gson.fromJson(LoginReturnJSONData, LoginJSONData.class);
                            if(mLoginJSONData.getGetLogin().equals("success"))
                            {
                                progressDialog.dismiss();
                                WriteLocalUserInformation(mLoginJSONData);//写入登录数据
                            }else if(mLoginJSONData.getGetLogin().equals("error")){
                                progressDialog.dismiss();
                                Toast.makeText(mContext,"登录失败,用户名或密码错误",Toast.LENGTH_SHORT).show();
                            }
                            Log.v("FairyDebug","登录结果："+mLoginJSONData.getGetLogin());
                        } catch (JsonSyntaxException e) {
                            progressDialog.dismiss();
                            Toast.makeText(mContext,"登录解析异常错误-2",Toast.LENGTH_SHORT).show();
                            Log.v("FairyDebug", "登录结果解析异常");
                        }
                    break;
                case 2:
                    progressDialog.dismiss();
                   // Toast.makeText(UserCenterFragment.this.getActivity(),"亲,您的网络状况不太好哦，请检查网络设置或稍后重试",Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext,"登录失败",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    private void UpdateUserLoginMethod() {

        SharedPreferences pref = UserCenterFragment.this.getActivity().getSharedPreferences("TempUserUIdFile", UserCenterFragment.this.getActivity().MODE_PRIVATE);
        int userUid = pref.getInt("tempUserUid",-1);

        if(userUid==-1)
        {
            Toast.makeText(mContext,"用户数据被非法破坏,请注销重新登陆",Toast.LENGTH_SHORT).show();
            return ;
        }

        //how to build  OnlySign
       // TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        WifiManager wm = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        String DEVICE_ID =m_szWLANMAC+"android_mobile";

        // execute post method
        MyPostHttpUtil.myPostHttpRequestUtils("http://122.114.58.68:8066/AppService/UserService.ashx?action=loginLimit", "&Uid="+userUid+"&OnlySign="+DEVICE_ID, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                loginLimitResultJSON=response;
                loginLimitResultHandler.sendEmptyMessage(1);

            }

            @Override
            public void onError(Exception e) {
                loginLimitResultHandler.sendEmptyMessage(2);
            }
        });

    }

    private Handler loginLimitResultHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    Gson loginlimitGson=new Gson();
                    LoginLimitResultBeans loginLimitResultBeans=loginlimitGson.fromJson(loginLimitResultJSON, LoginLimitResultBeans.class);

                    if(loginLimitResultBeans.getLoginLimit().trim().equals("SignNull"))
                    {
                        Toast.makeText(mContext,"登陆设备信息不能为空",Toast.LENGTH_SHORT).show();
                    }else if(loginLimitResultBeans.getLoginLimit().trim().equals("error"))
                    {
                        Toast.makeText(mContext,"未知异常",Toast.LENGTH_SHORT).show();
                    }else if(loginLimitResultBeans.getLoginLimit().trim().equals("success")){
                        //开启限制用户登陆检测功能服务
                        Intent startCheckLoginService=new Intent(mContext, LoginLimitCheckService.class);
                        mContext.startService(startCheckLoginService);
                        Toast.makeText(mContext,"登陆成功",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 2:
                    Toast.makeText(mContext,"登陆设备异常",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void WriteLocalUserInformation(LoginJSONData loginUser) {
        tempUserUid=Integer.valueOf(loginUser.getUid());
        String getUserInfoURL=getResources().getString(R.string.LoginURL)+"?action=getUserInfo&Uid="+tempUserUid;
        MyGetHttpUtil.mySendHttpRequestUtils(getUserInfoURL, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                //Log.v("FairyDebug","登录成功后根据Id获取用户信息-------------------------"+response);
                loginUserJSONData=response;
                getUserInfoHandler.sendEmptyMessage(1);

            }

            @Override
            public void onError(Exception e) {
                //Log.v("FairyDebug","获取用户信息失败异常"+e.toString());
                getUserInfoHandler.sendEmptyMessage(2);
            }
        });
    }

    private Handler getUserInfoHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    try {
                        Gson gson=new Gson();
                        UserInfo userInfo= gson.fromJson(loginUserJSONData, UserInfo.class);

                        SharePreferenceObjectTools.SharePreferencesSaveObject(
                                UserCenterFragment.this.getActivity(), "LoginUserShareference",
                                "LoginUserShareferenceKey", userInfo);

                        SharedPreferences.Editor editor =mContext.getSharedPreferences("TempUserUIdFile",
                                UserCenterFragment.this.getActivity().MODE_PRIVATE).edit();
                        editor.putInt("tempUserUid", tempUserUid);
                        editor.commit();

                        //更新当前用户登陆设备信息
                        UpdateUserLoginMethod();

                        UserCenterFragment.this.getActivity().finish();
                        //重新启动
                        Intent intent=new Intent(mContext,SplashActivity.class);
                        startActivity(intent);


                    } catch (JsonSyntaxException e) {
                        Toast.makeText(mContext, "获取用户信息失败,请检查网络设置或稍后重试", Toast.LENGTH_SHORT).show();
                    }


                    break;
                case 2:
                    //Log.v("FairyDebug","获取登陆用户信息失败");
                    break;
                default:
                    break;
            }
        }
    };






}
