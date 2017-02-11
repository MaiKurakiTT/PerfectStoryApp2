package cn.jinmiao.bbs.perfectstoryapp.fragment.home;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.BeforeSleepCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.EnglishListenCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.GXQMCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.HaveSoundHBCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.KWLGCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.KidNovelStoryCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.KidWorldCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.adapter.onePage.ShigesanwenStoryCataLogAdapter;
import cn.jinmiao.bbs.perfectstoryapp.bean.HomeCatalog;
import cn.jinmiao.bbs.perfectstoryapp.fragment.home.album.AlbumCateLogActivity;
import cn.jinmiao.bbs.perfectstoryapp.main.play.PlayMusicListActivity;
import cn.jinmiao.bbs.perfectstoryapp.main.search.SearchMusicActivity;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyGetHttpUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyHttpCallbackListenerUtil;

public class HomeFragment extends Fragment {
    private View homeView;// 首页视图控制器
    private ListView beforeSleepStoryListView;//睡前故事
    private ListView kidNovelStoryListView;//儿童小说
    private ListView shigesanwenListView;//诗歌散文
    private ListView guoxueqimengListView;//国学启蒙
    private ListView kewenlangduListView;//课文朗读
    private ListView childWorldListView;//儿童电台
    private ListView englishListenListView;//英语听力
    private ListView haveSoundHBListView;//有声绘本

    private String beforeSleepStoryJSONData;//睡前故事
    private String kidNovelJSONData;//儿童小说
    private String shigesanwenJSONData;//诗歌散文
    private String guoxueqimengJSONData;//国学启蒙
    private String kewenlangduJSONData;//课文朗读
    private String childWorldJSONData;//儿童电台
    private String englishListenJSONData;//英语听力
    private String haveSoundHBJSONData;//有声绘本

    private String getBeforeSleepStoryURL;//睡前故事URL
    private String getKidNovelURL;//儿童小说
    private String getShigeSanwenURL;//诗歌散文
    private String getGuoXueQiMengURL;//国学启蒙
    private String getkewenlangduURL;// 课文朗读
    private String getChildWorldURL;// 儿童电台
    private String getEnglishListenURL;// 英语听力
    private String haveSoundHBURL;//有声绘本

    private BeforeSleepCataLogAdapter beforeSleepCataLogAdapter;//睡前故事
    private KidNovelStoryCataLogAdapter kidNovelStoryCataLogAdapter;//儿童小说
    private ShigesanwenStoryCataLogAdapter shigesanwenStoryCataLogAdapter;//诗歌散文
    private GXQMCataLogAdapter gxqmCataLogAdapter;//国学启蒙
    private KWLGCataLogAdapter kwlgCataLogAdapter;// 课文朗读
    private KidWorldCataLogAdapter kidWorldCataLogAdapter;// 儿童电台
    private EnglishListenCataLogAdapter englishListenCataLogAdapter;//英语听力
    private HaveSoundHBCataLogAdapter haveSoundHBCataLogAdapter;// 有声绘本

    private List<HomeCatalog> homeCateLogList=new ArrayList<HomeCatalog>();// 睡前故事
    private List<HomeCatalog> kidNovelStoryCateLogList=new ArrayList<HomeCatalog>();//儿童小说
    private List<HomeCatalog> shigesanwenyCateLogList=new ArrayList<HomeCatalog>();//诗歌散文
    private List<HomeCatalog> guoxueqimengCateLogList=new ArrayList<HomeCatalog>();// 国学启蒙
    private List<HomeCatalog> kewenlangduCateLogList=new ArrayList<HomeCatalog>();// 课文朗读
    private List<HomeCatalog> childWorldCateLogList=new ArrayList<HomeCatalog>();// 儿童电台
    private List<HomeCatalog> englishListenList=new ArrayList<HomeCatalog>();// 英语听力
    private List<HomeCatalog> havaSoundHBList=new ArrayList<HomeCatalog>();// 有声绘本

    private ProgressDialog progressDialog;// 加载进度框

    private ImageView nowPlaySinglistPageImg;
    //正在播放列表

    private ImageView search_music_Home_Btn;// 搜索

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeView=inflater.inflate(R.layout.fragment_homeitem_list,container,false);
     //   Log.v("FairyDebug","HomeFragment --------OnCreateView ");

        //初始化界面找Id
        initView();

        //当歌曲列表图标单击触发事件
        currentPlayListImgOnclickMethod();

        //获取网络数据
        getWebData();

        return homeView;
    }

    //适配器适配每一个item单击后跳转到专辑列表界面
    public void JumpAlbumActivityMethod(String  id,String albumCateName){


        Log.v("FairyDebug","----HomeFragment ---->  AlbumCateLogActivity -----专辑Id=["+id+"]---专辑名称=["+albumCateName+"]");

        //HomeFragment ---------->  AlbumCateLogActivity
        Intent intent=new Intent(getActivity(),AlbumCateLogActivity.class);
        intent.putExtra("uid", id);//专辑Id
        intent.putExtra("albumCateName", albumCateName);//专辑名称
        startActivity(intent);
        HomeFragment.this.getActivity().finish();//结束防止叠屏

    }

    //当前正在播放列表
    private void currentPlayListImgOnclickMethod() {
        nowPlaySinglistPageImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //尝试读取文件中的最近播放列表数据判断是否可以跳转
                SharedPreferences pref = HomeFragment.this.getActivity().getSharedPreferences("currentPlayAlbumDataFile", HomeFragment.this.getActivity().MODE_PRIVATE);
                Integer currentPlayListAlbumId = pref.getInt("currentPlayListAlbumId", -1);//读取本地SharePreference文件 查找currentPlayListAlbumId的值，如果没有返回-1
                String mAlbumName=pref.getString("currentAlbumName",null);

                if (currentPlayListAlbumId != -1&&mAlbumName!=null )// 当前有正在播放列表  当音乐被单击后
                {
                    //从MainActivity 进入播放列表界面
                    Intent intent = new Intent(HomeFragment.this.getActivity(), PlayMusicListActivity.class);
                    intent.putExtra("mAlbumId", currentPlayListAlbumId+"");//专辑Id 获取播放列表
                    intent.putExtra("mAlbumName",mAlbumName);//专辑名称
                    startActivity(intent);
                } else {// 当前无最近播放列表
                    Toast.makeText(HomeFragment.this.getActivity(), "当前无最近播放列表", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //初始化界面找Id
    private void initView() {
        //睡前故事
        beforeSleepStoryListView= (ListView)homeView.findViewById(R.id.beforeSleepStoryListView_id);
        //儿童小说
        kidNovelStoryListView=(ListView)homeView.findViewById(R.id.kidNovelListView_id);
        //诗歌散文
        shigesanwenListView=(ListView)homeView.findViewById(R.id.shigesanwen_ListView_id);
        //国学启蒙
        guoxueqimengListView=(ListView)homeView.findViewById(R.id.guoxueqimeng_ListView_id);
        //课文朗读
        kewenlangduListView=(ListView)homeView.findViewById(R.id.kewenlangduListView_id);
        //儿童电台
        childWorldListView=(ListView)homeView.findViewById(R.id.kidWorldListView_id);
        //英语听力
        englishListenListView=(ListView)homeView.findViewById(R.id.EnglishListenning_ListView_id);
        //有声绘本
        haveSoundHBListView=(ListView)homeView.findViewById(R.id.haveSoundHuiben_ListView_id);

        //正在播放列表
        nowPlaySinglistPageImg=(ImageView)homeView.findViewById(R.id.nowPlaySingListPageImg_id);

        //搜索歌曲
        search_music_Home_Btn=(ImageView)homeView.findViewById(R.id.search_music_Home_Btn_id);
        search_music_Home_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent=new Intent(HomeFragment.this.getActivity(), SearchMusicActivity.class);
                startActivity(searchIntent);
                HomeFragment.this.getActivity().finish();
            }
        });

    }

/********************************************************************************************************
 * 请求获取网络数据家在列表
 **************************************************************************************************** ***/
    private void getWebData() {

        // 打开加载数据对话框
        progressDialog = ProgressDialog.show(getActivity(), "请稍等...", "正在加载数据...", true);

        //1. 睡前故事
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
        });

        //儿童小说
        getKidNovelURL=getString(R.string.getOneCatalogStoryBaseURL)+"?action=getTwoColumns&ID=2";
//Log.v("获取一级标题栏目儿童小说目录URL",getKidNovelURL);
        MyGetHttpUtil.mySendHttpRequestUtils(getKidNovelURL, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                getKidNovelStoryHandle.sendEmptyMessage(1);
                Log.v("发送一级标题获取儿童小说JSON内容", response);
                kidNovelJSONData = response;
            }

            @Override
            public void onError(Exception e) {
                getKidNovelStoryHandle.sendEmptyMessage(2);
                kidNovelJSONData = e.toString();
                Log.v("发送一级标题获取JSON失败原因", e.toString());
                return ;
            }
        });

        // 诗歌散文
        getShigeSanwenURL=getString(R.string.getOneCatalogStoryBaseURL)+"?action=getTwoColumns&ID=4";
//Log.v("获取一级标题栏目诗歌散文目录URL",getShigeSanwenURL);
        MyGetHttpUtil.mySendHttpRequestUtils(getShigeSanwenURL, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                getShigesanwenStoryHandle.sendEmptyMessage(1);
                Log.v("发送一级标题获取诗歌散文JSON内容", response);
                shigesanwenJSONData = response;
            }

            @Override
            public void onError(Exception e) {
                getShigesanwenStoryHandle.sendEmptyMessage(2);
                shigesanwenJSONData = e.toString();
                Log.v("发送一级标题获取JSON失败原因", e.toString());
                return ;
            }
        });

        // 国学启蒙
        getGuoXueQiMengURL=getString(R.string.getOneCatalogStoryBaseURL)+"?action=getTwoColumns&ID=5";
//Log.v("获取一级标题栏目国学启蒙目录URL",getGuoXueQiMengURL);
        MyGetHttpUtil.mySendHttpRequestUtils(getGuoXueQiMengURL, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                getGXQMStoryHandle.sendEmptyMessage(1);
                Log.v("发送一级标题获取国学启蒙JSON内容", response);
                guoxueqimengJSONData = response;
            }

            @Override
            public void onError(Exception e) {
                getGXQMStoryHandle.sendEmptyMessage(2);
                guoxueqimengJSONData = e.toString();
                Log.v("发送一级标题获取JSON失败原因", e.toString());
                return ;
            }
        });

        //课文朗读
        getkewenlangduURL=getString(R.string.getOneCatalogStoryBaseURL)+"?action=getTwoColumns&ID=6";
//Log.v("获取一级标题栏目课文朗读目录URL",getkewenlangduURL);
        MyGetHttpUtil.mySendHttpRequestUtils(getkewenlangduURL, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                getKWLDtoryHandle.sendEmptyMessage(1);
                Log.v("发送一级标题获取课文朗读JSON内容", response);
                kewenlangduJSONData = response;
            }

            @Override
            public void onError(Exception e) {
                getKWLDtoryHandle.sendEmptyMessage(2);
                kewenlangduJSONData = e.toString();
                Log.v("发送一级标题获取JSON失败原因", e.toString());
                return ;
            }
        });
        //儿童电台
        getChildWorldURL=getString(R.string.getOneCatalogStoryBaseURL)+"?action=getTwoColumns&ID=7";
//Log.v("获取一级标题栏目儿童电台目录URL",getChildWorldURL);
        MyGetHttpUtil.mySendHttpRequestUtils(getChildWorldURL, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                getchildWorldHandle.sendEmptyMessage(1);
                Log.v("发送一级标题获取儿童电台JSON内容", response);
                childWorldJSONData = response;
            }

            @Override
            public void onError(Exception e) {
                getchildWorldHandle.sendEmptyMessage(2);
                childWorldJSONData = e.toString();
                Log.v("发送一级标题获取JSON失败原因", e.toString());
                return ;
            }
        });
        //英语听力
        getEnglishListenURL=getString(R.string.getOneCatalogStoryBaseURL)+"?action=getTwoColumns&ID=8";
//Log.v("获取一级标题栏目英语听力目录URL",getEnglishListenURL);
        MyGetHttpUtil.mySendHttpRequestUtils(getEnglishListenURL, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                getEnglishListenHandle.sendEmptyMessage(1);
                Log.v("发送一级标题获取英语听力JSON内容", response);
                englishListenJSONData = response;
            }

            @Override
            public void onError(Exception e) {
                getEnglishListenHandle.sendEmptyMessage(2);
                englishListenJSONData = e.toString();
                Log.v("发送一级标题获取JSON失败原因", e.toString());
                return ;
            }
        });
        //有声绘本
        haveSoundHBURL=getString(R.string.getOneCatalogStoryBaseURL)+"?action=getTwoColumns&ID=116";
//Log.v("获取一级标题栏目有声绘本目录URL",haveSoundHBURL);
        MyGetHttpUtil.mySendHttpRequestUtils(haveSoundHBURL, new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                getHaveSoundHBHandle.sendEmptyMessage(1);
                Log.v("发送一级标题获取有声绘本JSON内容", response);
                haveSoundHBJSONData = response;
            }

            @Override
            public void onError(Exception e) {
                getHaveSoundHBHandle.sendEmptyMessage(2);
                haveSoundHBJSONData = e.toString();
                Log.v("发送一级标题获取JSON失败原因", e.toString());
                return ;
            }
        });


    }

    //睡前故事
    private Handler getBeforeSleepStoryHandle =new Handler() {

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 1:
                   try {
                       Gson gson=new Gson();
                       homeCateLogList= gson.fromJson(beforeSleepStoryJSONData,new TypeToken<List<HomeCatalog>>() {}.getType());
                       beforeSleepCataLogAdapter=new BeforeSleepCataLogAdapter(HomeFragment.this,homeView.getContext(),homeCateLogList);
                       beforeSleepStoryListView.setAdapter(beforeSleepCataLogAdapter);
                   } catch (JsonSyntaxException e) {
                       progressDialog.dismiss();
                       e.printStackTrace();
                   }
                   progressDialog.dismiss();
                   break;
               case 2:
                   progressDialog.dismiss();
                   Toast.makeText(getActivity(),"服务器连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                   break;
               default:
                   break;
           }
        }
    };
    // 儿童小说
    private Handler getKidNovelStoryHandle =new Handler() {

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 1:
                   Gson gson=new Gson();
                   kidNovelStoryCateLogList= gson.fromJson(kidNovelJSONData,new TypeToken<List<HomeCatalog>>() {}.getType());
                   kidNovelStoryCataLogAdapter=new KidNovelStoryCataLogAdapter(HomeFragment.this,homeView.getContext(),kidNovelStoryCateLogList);
                   kidNovelStoryListView.setAdapter(kidNovelStoryCataLogAdapter);
                   break;
               case 2:
                   Toast.makeText(getActivity(),"服务器连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                   break;
               default:
                   break;
           }
        }
    };
    // 诗歌散文
    private Handler getShigesanwenStoryHandle =new Handler() {

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 1:
                   Gson gson=new Gson();
                   shigesanwenyCateLogList= gson.fromJson(shigesanwenJSONData,new TypeToken<List<HomeCatalog>>() {}.getType());
                   shigesanwenStoryCataLogAdapter=new ShigesanwenStoryCataLogAdapter(HomeFragment.this,homeView.getContext(),shigesanwenyCateLogList);
                   shigesanwenListView.setAdapter(shigesanwenStoryCataLogAdapter);
                   break;
               case 2:
                   Toast.makeText(getActivity(),"服务器连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                   break;
               default:
                   break;
           }
        }
    };
    // 国学启蒙
    private Handler getGXQMStoryHandle =new Handler() {

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 1:
                   Gson gson=new Gson();
                   guoxueqimengCateLogList= gson.fromJson(guoxueqimengJSONData,new TypeToken<List<HomeCatalog>>() {}.getType());
                   gxqmCataLogAdapter=new GXQMCataLogAdapter(HomeFragment.this,homeView.getContext(),guoxueqimengCateLogList);
                   guoxueqimengListView.setAdapter(gxqmCataLogAdapter);
                   break;
               case 2:
                   Toast.makeText(getActivity(),"服务器连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                   break;
               default:
                   break;
           }
        }
    };
    // 课文朗读
    private Handler getKWLDtoryHandle =new Handler() {

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 1:
                   Gson gson=new Gson();
                   kewenlangduCateLogList= gson.fromJson(kewenlangduJSONData,new TypeToken<List<HomeCatalog>>() {}.getType());
                   kwlgCataLogAdapter=new KWLGCataLogAdapter(HomeFragment.this,homeView.getContext(),kewenlangduCateLogList);
                   kewenlangduListView.setAdapter(kwlgCataLogAdapter);
                   break;
               case 2:
                   Toast.makeText(HomeFragment.this.getActivity(),"服务器连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                   break;
               default:
                   break;
           }
        }
    };
    // 儿童电台
    private Handler getchildWorldHandle =new Handler() {

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 1:
                   Gson gson=new Gson();
                   childWorldCateLogList= gson.fromJson(childWorldJSONData,new TypeToken<List<HomeCatalog>>() {}.getType());
                   kidWorldCataLogAdapter=new KidWorldCataLogAdapter(HomeFragment.this,homeView.getContext(),childWorldCateLogList);
                   childWorldListView.setAdapter(kidWorldCataLogAdapter);
                   break;
               case 2:
                   Toast.makeText(getActivity(),"服务器连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                   break;
               default:
                   break;
           }
        }
    };
    // 英语听力
    private Handler getEnglishListenHandle =new Handler() {

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 1:
                   Gson gson=new Gson();
                   englishListenList= gson.fromJson(englishListenJSONData,new TypeToken<List<HomeCatalog>>() {}.getType());
                   englishListenCataLogAdapter=new EnglishListenCataLogAdapter(HomeFragment.this,homeView.getContext(),englishListenList);
                   englishListenListView.setAdapter(englishListenCataLogAdapter);
                   break;
               case 2:
                   Toast.makeText(getActivity(),"服务器连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                   break;
               default:
                   break;
           }
        }
    };
    //有声绘本
    private Handler getHaveSoundHBHandle =new Handler() {

        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 1:
                   Gson gson=new Gson();
                   havaSoundHBList= gson.fromJson(haveSoundHBJSONData,new TypeToken<List<HomeCatalog>>() {}.getType());
                   haveSoundHBCataLogAdapter=new HaveSoundHBCataLogAdapter(HomeFragment.this,homeView.getContext(),havaSoundHBList);
                   haveSoundHBListView.setAdapter(haveSoundHBCataLogAdapter);
                   break;
               case 2:
                   Toast.makeText(getActivity(),"服务器连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                   break;
               default:
                   break;
           }
        }
    };
    /********************************************************************************************************
     * 请求获取网络数据家在列表
     **************************************************************************************************** ***/
}
