package cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.aboutus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.utils.AppCmpaActivityCollector;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCmpaActivityCollector.addAppCompatActivity(this);
        setContentView(R.layout.activity_contact_us);


       ImageView comebackBtn=(ImageView)findViewById(R.id.comeback_contanct_us);
        comebackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUsActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCmpaActivityCollector.removeAppCompatActivity(this);
    }
}
