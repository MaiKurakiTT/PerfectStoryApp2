package cn.jinmiao.bbs.perfectstoryapp.main.search;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.AlbumCatelog;
import cn.jinmiao.bbs.perfectstoryapp.bean.SearchResultBean;
import cn.jinmiao.bbs.perfectstoryapp.main.MainActivity;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyHttpCallbackListenerUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyPostHttpUtil;

// 搜索界面
public class SearchMusicActivity extends AppCompatActivity {
    private ImageView comebackSearchBtn;
    private EditText searchConditionEditext;
    private TextView commit;

    private String searchMusicResult=null;
    private List<SearchResultBean> searchResultAlbumList=new ArrayList<SearchResultBean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCmpaActivityCollector.addAppCompatActivity(this);
        setContentView(R.layout.activity_search_music);

        comebackSearchBtn= (ImageView)findViewById(R.id.finishedSearchBtn_id);
        searchConditionEditext=(EditText)findViewById(R.id.findConditionEdittext_id);
        commit = (TextView) findViewById(R.id.search_commit);
        comebackSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchMusicActivity.this, MainActivity.class);
                startActivity(intent);
                SearchMusicActivity.this.finish();
            }
        });
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SearchMusicActivity.this,"正在搜索,请稍后...",Toast.LENGTH_SHORT).show();
                GoToSearcchResult();
            }
        });



       /* searchConditionEditext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId== EditorInfo.IME_ACTION_SEND ||
                        (event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
                {
                    Toast.makeText(SearchMusicActivity.this,"正在搜索,请稍后...",Toast.LENGTH_SHORT).show();
                    GoToSearcchResult();
                }
                return false;
            }
        });*/


    }

    private void GoToSearcchResult() {

        String searchMsg=searchConditionEditext.getText().toString().trim();
        try {
            searchMsg  = URLEncoder.encode(searchMsg,"UTF-8");
        } catch (UnsupportedEncodingException e) {
           Log.v("FariyDebug","URL转码失败");
        }
        MyPostHttpUtil.myPostHttpRequestUtils(getString(R.string.getMusicListURL)+"?","action=serch&Message="+searchMsg, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                searchMusicResult=response;
                dealWithSearchMusicHandler.sendEmptyMessage(15);
            }

            @Override
            public void onError(Exception e) {
                dealWithSearchMusicHandler.sendEmptyMessage(16);
            }
        });
    }

    private Handler dealWithSearchMusicHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 15:
                    if(searchMusicResult!=null|| searchMusicResult.trim().equals("[]"))
                    {

                        try {
                            Gson searchResultGson=new Gson();
                            searchResultAlbumList=searchResultGson.fromJson(searchMusicResult,new TypeToken<List<SearchResultBean>>() {}.getType());
                            Intent goToSearchResultIntent = new Intent(SearchMusicActivity.this, SearchPlayListActivity.class);
                            goToSearchResultIntent.putExtra("searchResultList",(Serializable)searchResultAlbumList);
                            startActivity(goToSearchResultIntent);
                        } catch (JsonSyntaxException e) {
                            Toast.makeText(SearchMusicActivity.this,"查询结果解析失败,请检查网络连接",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(SearchMusicActivity.this,"没有找到相关故事",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 16:
                    Toast.makeText(SearchMusicActivity.this,"查询失败,请检查网络设置",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCmpaActivityCollector.removeAppCompatActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 键盘码如果是返回键 则返回上一页面
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(SearchMusicActivity.this, MainActivity.class);
            startActivity(intent);
            SearchMusicActivity.this.finish();
        }
        return false;
    }


}
