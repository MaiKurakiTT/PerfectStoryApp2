package cn.jinmiao.bbs.perfectstoryapp.fragment.home.album;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.adapter.AlBumAdapter;
import cn.jinmiao.bbs.perfectstoryapp.bean.AlbumCatelog;
import cn.jinmiao.bbs.perfectstoryapp.main.MainActivity;
import cn.jinmiao.bbs.perfectstoryapp.main.play.PlayMusicListActivity;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyGetHttpUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyHttpCallbackListenerUtil;

public class AlbumCateLogActivity extends AppCompatActivity implements OnClickListener{
    private ListView albumListView;
    private ImageView comeBackImgView;
    private TextView alBumCateNametitleTextView;

    private AlBumAdapter alBumAdapter;
    private List<AlbumCatelog> albumCatelogList=new ArrayList<AlbumCatelog>();

    private Integer albumUid;//专辑Id
    private String albumName;//专辑名称

    private String getALbumListURL;
    private String getAlbumJSONData;

    private ProgressDialog progressDialog;// 加载进度框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  Log.v("FairyDebug", "AlbumCateLogActivity --------create ");
        AppCmpaActivityCollector.addAppCompatActivity(this);
        setContentView(R.layout.activity_album_cate_log);

        findViewById();

        loadWebData();
    }




    private void loadWebData() {

        // 打开加载数据对话框
        progressDialog = ProgressDialog.show(AlbumCateLogActivity.this,
                "请稍等...", "正在加载数据...", true);

        try {
            // 从上一页面获取专辑Id ,根据专辑Id 获取专辑列表
            albumUid=Integer.valueOf(getIntent().getStringExtra("uid"));

            // 从上一界面获取专辑名称
            albumName=getIntent().getStringExtra("albumCateName");
            alBumCateNametitleTextView.setText(""+albumName);
        } catch (NumberFormatException e) {
           // Toast.makeText(AlbumCateLogActivity.this,"网络连接失败,请检查网络设置或稍后重试",Toast.LENGTH_SHORT).show();
            return ;
        }

        if(albumUid!=null&&albumName!=null)
        {
            //构造获取专辑列表URL
            getALbumListURL= getString(R.string.getOneCatalogStoryBaseURL)+"?action=getAlbum&AlbumID="+albumUid;
           // Log.v("FairyDebug", "获取专辑URL---------" + getALbumListURL);

            MyGetHttpUtil.mySendHttpRequestUtils(getALbumListURL, new MyHttpCallbackListenerUtil() {
                @Override
                public void onFinish(String response) {
                    getAlbumJSONData = response;
                    getAlbumListHandle.sendEmptyMessage(3);
                }

                @Override
                public void onError(Exception e) {
                    getAlbumListHandle.sendEmptyMessage(4);
                }
            });
        }else{
            //Toast.makeText(AlbumCateLogActivity.this,"网络连接失败,请检查网络设置",Toast.LENGTH_SHORT).show();
            return ;
        }
    }

    private Handler getAlbumListHandle=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 3:

                    try {
                        Gson gson=new Gson();
                        albumCatelogList=gson.fromJson(getAlbumJSONData,new TypeToken<List<AlbumCatelog>>() {}.getType());
                        alBumAdapter=new AlBumAdapter(AlbumCateLogActivity.this,AlbumCateLogActivity.this.getApplicationContext(),albumCatelogList);
                        albumListView.setAdapter(alBumAdapter);

                    } catch (JsonSyntaxException e) {
                        Toast.makeText(AlbumCateLogActivity.this,"数据解析异常-1",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    progressDialog.dismiss();

                    break;
                case 4:
                    Toast.makeText(AlbumCateLogActivity.this,"加载数据失败,请检查网络设置或稍后重试",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private void findViewById() {

        alBumCateNametitleTextView=(TextView)findViewById(R.id.album_Title_id);

        albumListView=(ListView)findViewById(R.id.albumList_ListView_id);

        comeBackImgView=(ImageView)findViewById(R.id.music_list_comeback_ImageView);
        comeBackImgView.setOnClickListener(this);
    }

    //专辑列表跳转到播放列表
    public void JumpPlayListActivityMethod(String uid,String albumName){

        // AlbumCateLogActivity------------> PlayMusicListActivity
//童话故事分类
        //跳转到播放列表界面
        Intent intent=new Intent(AlbumCateLogActivity.this,PlayMusicListActivity.class);
        intent.putExtra("mAlbumId", uid);//专辑Id
        intent.putExtra("mAlbumName",albumName);//专辑名称
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.music_list_comeback_ImageView:
                Intent backHomeViewIntent=new Intent(AlbumCateLogActivity.this,MainActivity.class);
                startActivity(backHomeViewIntent);
                AlbumCateLogActivity.this.finish();
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 键盘码如果是返回键 则返回上一页面
        if(keyCode==KeyEvent.KEYCODE_BACK){
            /*Intent backHomeViewIntent=new Intent(AlbumCateLogActivity.this,MainActivity.class);
            startActivity(backHomeViewIntent);
            AlbumCateLogActivity.this.finish();*/
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("FairyDebug", "AlbumCateLogActivity --------OnDestroy() ");
        AppCmpaActivityCollector.removeAppCompatActivity(this);
    }
}
