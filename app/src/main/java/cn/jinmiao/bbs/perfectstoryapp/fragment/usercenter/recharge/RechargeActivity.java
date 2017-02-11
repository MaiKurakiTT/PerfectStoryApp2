package cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.recharge;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.custom.WebViewWithProgress;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;

public class RechargeActivity extends AppCompatActivity {

    private WebViewWithProgress myWebViewWithProgress;
    private WebView myWebView;
    private TextView exit_RechargeTextview;
//    private  UserInfo mCurrentLoginUser;//当前登录用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCmpaActivityCollector.addAppCompatActivity(this);
        setContentView(R.layout.activity_recharge);

        findViewById();


        myWebView=myWebViewWithProgress.getWebView();

        // 设置页面支持JavaScript
        myWebView.getSettings().setJavaScriptEnabled(true);

        //禁用缓存
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        //缩放开关
        //设置此属性，仅支持双击缩放，不支持触摸缩放（android4.0）
        myWebView.getSettings().setSupportZoom(true);
        // 设置是否可缩放
        myWebView.getSettings().setBuiltInZoomControls(false);
        //将图片调整到适合WebView的大小
        myWebView.getSettings().setUseWideViewPort(false);

        //修复WebView远程代码执行
        myWebView.removeJavascriptInterface("searchBoxJavaBredge_");

        //设置图片自动加载
        myWebView.getSettings().setLoadsImagesAutomatically(true);

       // mCurrentLoginUser=(UserInfo) SharePreferenceObjectTools.readSharePreferencesObject(RechargeActivity.this, "LoginUserShareference", "LoginUserShareferenceKey");

        //隐藏模式
        myWebView.setVisibility(View.VISIBLE);
        // 加载URL
        // myWebView.loadUrl("http://simhy.com/page.php");
       // myWebView.loadUrl("http://www.zc532.com/login.php?uid=19"+"&salt="+mCurrentLoginUser.getSalt());

      //  myWebView.loadUrl("http://www.zc532.com/bbs/plugin.php?id=dc_vip&action=pay");

        // 设置WebView监听事件
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                if(url.startsWith("http:") || url.startsWith("https:") ) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
               // Log.v("FairyDebug","当前结束后URL"+url);

            }

        });
        myWebView.loadUrl("http://m.zc532.com/bbs/plugin.php?id=dc_vip&action=pay");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCmpaActivityCollector.removeAppCompatActivity(this);
    }

    private void findViewById() {
        myWebViewWithProgress=(WebViewWithProgress)findViewById(R.id.my_recharge_WebView_id);
        exit_RechargeTextview=(TextView)findViewById(R.id.exit_recharge_id);
        exit_RechargeTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               RechargeActivity.this.finish();
            }
        });
    }


    private void ExitDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(RechargeActivity.this);

        builder.setMessage("确定要返回吗?");
        builder.setTitle("信息");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                RechargeActivity.this.finish();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        // 键盘码如果是返回键 则返回上一页面
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }else if((keyCode==KeyEvent.KEYCODE_BACK)&& !myWebView.canGoBack()){
            ExitDialog();
            //finish();// 结束退出程序
        }
        return false;
    }
}
