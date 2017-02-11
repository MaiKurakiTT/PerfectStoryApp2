package cn.jinmiao.bbs.perfectstoryapp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.BeforeSleepCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.ShigesanwenStoryCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.bean.HomeCatalog;
import cn.jinmiao.bbs.perfectstoryapp.fragment.home.HomeFragment;
import cn.jinmiao.bbs.perfectstoryapp.fragment.home.album.AlbumCateLogActivity;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyGetHttpUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyHttpCallbackListenerUtil;

/**
 * Created by Administrator on 2016/12/15 0015.
 */

public class SecondMenuActivity extends AppCompatActivity {

    private int position;

    private int mLocation;

    private ProgressDialog progressDialog;// 加载进度框

    private GridView gridView;

    private TextView textView;

    private String text;

    private ImageView mReturn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m_secondmenu_activity);

        AppCmpaActivityCollector.addAppCompatActivity(this);

        mLocation = getIntent().getIntExtra("position" , 0);
        text = getIntent().getStringExtra("text");

        position = getIntent().getIntExtra("position" , 0) + 1;
        switch (position){
            case 3: position=4;
                break;
            case 4: position=5;
                break;
            case 5 : position=6;
                break;
            case 6: position=7;
                break;
            case 7 : position=8;
                break;
            case 8:position=116;
                break;

        }
        progressDialog = ProgressDialog.show(this, "请稍等...", "正在加载数据...", true);
        
        initView();
        getNetDate();
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.m_secondmenu_gv);
        textView = (TextView) findViewById(R.id.bar_top_title);
        mReturn = (ImageView) findViewById(R.id.search_music_Home_Btn_id);
        textView.setText(text);
        /*mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

    }




    private void getNetDate(){
        MyGetHttpUtil.mySendHttpRequestUtils(getString(R.string.getOneCatalogStoryBaseURL) + "?action=getTwoColumns&ID=" + position, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                Log.i("mySendHttpRequestUtils",response);
                // [{"id":"57","title":"童话故事"},{"id":"58","title":"神话故事"},{"id":"60","title":"寓言故事"},{"id":"61","title":"成语故事"},{"id":"62","title":"生活百科"},{"id":"108","title":"历史故事"},{"id":"137","title":"情绪合集"}]
               // Gson gson=new Gson();
                // List<HomeCatalog> list= gson.fromJson(response,new TypeToken<List<HomeCatalog>>() {}.getType());
//                beforeSleepCataLogAdapter=new BeforeSleepCataLogAdapter(HomeFragment.this,homeView.getContext(),homeCateLogList);
//                beforeSleepStoryListView.setAdapter(beforeSleepCataLogAdapter);
               // gridView.setAdapter(new SecondMenuAdapter(SecondMenuActivity.this,list));
                Message msg = new Message();
                msg.what = 1;
                msg.obj = response;
                handler.sendMessage(msg);

                progressDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }


    private Handler handler =new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    Gson gson=new Gson();
                    List<HomeCatalog> lists = gson.fromJson(msg.obj.toString(),new TypeToken<List<HomeCatalog>>() {}.getType());
                    SecondMenuAdapter adapter = new SecondMenuAdapter(SecondMenuActivity.this,lists,mLocation);
                    gridView.setAdapter(adapter);

                    break;
                case 2:
                    Toast.makeText(SecondMenuActivity.this,"服务器连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    public void JumpAlbumActivityMethod(String  id,String albumCateName){


        Log.v("FairyDebug","----HomeFragment ---->  AlbumCateLogActivity -----专辑Id=["+id+"]---专辑名称=["+albumCateName+"]");

        //HomeFragment ---------->  AlbumCateLogActivity
        Intent intent=new Intent(this,AlbumCateLogActivity.class);
        intent.putExtra("uid", id);//专辑Id
        intent.putExtra("albumCateName", albumCateName);//专辑名称
        startActivity(intent);

    }

/*    //1. 睡前故事
    getBeforeSleepStoryURL=getString(R.string.getOneCatalogStoryBaseURL)+"?action=getTwoColumns&ID=1";
//Log.v("获取一级标题栏目睡前故事目录URL",getBeforeSleepStoryURL);
    MyGetHttpUtil.mySendHttpRequestUtils(getBeforeSleepStoryURL, new MyHttpCallbackListenerUtil() {
        @Override
        public void onFinish(String response) {
            getBeforeSleepStoryHandle.sendEmptyMessage(1);
            Log.v("发送一级标题获取儿童小说JSON内容", response);
            beforeSleepStoryJSONData = response;
        }

        @Override
        public void onError(Exception e) {
            getBeforeSleepStoryHandle.sendEmptyMessage(2);
            beforeSleepStoryJSONData = e.toString();
            Log.v("发送一级标题获取JSON失败原因", e.toString());
            return ;
        }
    });*/
}
