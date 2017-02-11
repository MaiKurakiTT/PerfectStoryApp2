package cn.jinmiao.bbs.perfectstoryapp.main.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.adapter.SearchResultAdapter;
import cn.jinmiao.bbs.perfectstoryapp.bean.AlbumCatelog;
import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;
import cn.jinmiao.bbs.perfectstoryapp.bean.SearchResultBean;
import cn.jinmiao.bbs.perfectstoryapp.main.play.PlayMusicListActivity;
import cn.jinmiao.bbs.perfectstoryapp.main.play.ShowContentActivity;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppConstant;
import cn.jinmiao.bbs.perfectstoryapp.utils.SharePreferenceObjectTools;

public class SearchPlayListActivity extends AppCompatActivity {
    private ImageView searchPlayListComeBack;
    private ListView searchResultListView;
    private List<SearchResultBean> searchResultBeanList=new ArrayList<SearchResultBean>();

    private SearchResultAdapter searchResultAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCmpaActivityCollector.addAppCompatActivity(this);
        setContentView(R.layout.activity_search_play_list);

        searchPlayListComeBack=(ImageView)findViewById(R.id.music_SearchResult_list_comeback_ImageView);
        searchResultListView=(ListView)findViewById(R.id.music_SearchResult_ListView_id);

        searchPlayListComeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPlayListActivity.this.finish();
            }
        });


        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                deatlWithRestulHandler.sendEmptyMessage(1);
            }
        });

    }

    private Handler deatlWithRestulHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    InitSearchResultData();
                    break;
                default:
                    break;
            }
        }
    };

    private void InitSearchResultData() {

        searchResultBeanList=(List<SearchResultBean>)getIntent().getSerializableExtra("searchResultList");

        searchResultAdapter=new SearchResultAdapter(SearchPlayListActivity.this,SearchPlayListActivity.this.getApplicationContext(),searchResultBeanList);

        searchResultListView.setAdapter(searchResultAdapter);
    }

    //专辑列表跳转到播放列表
    public void JumpPlayListActivityMethod(String uid,String albumName){


//童话故事分类
        //跳转到播放列表界面
        Intent intent=new Intent(SearchPlayListActivity.this,PlayMusicListActivity.class);
        intent.putExtra("mAlbumId", uid);//专辑Id
        intent.putExtra("mAlbumName",albumName);//专辑名称
        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCmpaActivityCollector.removeAppCompatActivity(this);
    }
}
