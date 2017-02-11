package cn.jinmiao.bbs.perfectstoryapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.fragment.home.HomeFragment;
import cn.jinmiao.bbs.perfectstoryapp.main.search.SearchMusicActivity;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class MyHomeFragment extends Fragment {

    //根界面
    private View mRootView;
    //九宫格
    private GridView mGridView;
    //轮播图
    private ViewPager mViewPager;
    // 搜索
    private ImageView search_music_Home_Btn;

    private int[] icons = new int[]{
            R.mipmap.lbt1,
            R.mipmap.lbt2,
            R.mipmap.lbt3
    };


    //
    private List<String> dates = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.m_home_fragment,container,false);
        mGridView = (GridView) mRootView.findViewById(R.id.m_home_fragment_gv);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.m_home_fragment_vp);
        
        initView();

        String[] items = getResources().getStringArray(R.array.names);
        for (String item : items){
            dates.add(item);
        }
        MyHomeAdapter adapter = new MyHomeAdapter(getActivity(),dates);
        List<View> lists = new ArrayList<View>();
        for(int i = 0 ; i<icons.length;i++){
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(icons[i]);
            lists.add(imageView);
        }
        MyHomeViewpagerAdapter pageAdapter = new MyHomeViewpagerAdapter(getActivity(),lists);
        pageAdapter.setIcons(icons);
        mGridView.setAdapter(adapter);
        mViewPager.setAdapter(pageAdapter);
        return mRootView;
    }

    private void initView() {
        search_music_Home_Btn=(ImageView)mRootView.findViewById(R.id.search_music_Home_Btn_id);
        search_music_Home_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent=new Intent(MyHomeFragment.this.getActivity(), SearchMusicActivity.class);
                startActivity(searchIntent);
                MyHomeFragment.this.getActivity().finish();
            }
        });
    }

}
