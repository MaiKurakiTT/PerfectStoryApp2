package cn.jinmiao.bbs.perfectstoryapp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import cn.jinmiao.bbs.perfectstoryapp.R;

/**
 * Created by Administrator on 2016/12/15 0015.
 */

public class ThreeMenuActivity extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.m_threemenu_activity);
    
        initView();

    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview);
    }
}
