package cn.jinmiao.bbs.perfectstoryapp.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.recharge.RechargeActivity;
import cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.register.RegisterPageActivity;

/**
 * Created by Administrator on 2017/1/3 0003.
 */

public class ShowDialogActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        android.app.AlertDialog.Builder alter = new AlertDialog.Builder(getApplicationContext());
        alter.setTitle("提示")
                .setMessage("好好听故事网提醒：\n" +
                        "\n" +
                        "一 教育投资要尽早，敢收费是最好的。\n" +
                        "\n" +
                        "二 播音员录制，十一五课题，2万音频。\n" +
                        "\n" +
                        "三 全部试听90秒，付费收听完整版。\n" +
                        "\n" +
                        "四 支持手机APP，年付192元，月付20元。\n" +
                        "\n" +
                        "五 让天下孩子都能有健康高尚人格。")
                .setPositiveButton("以后再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("成为VIP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Intent jumpRegisterIntent=new Intent(ShowDialogActivity.this,RegisterPageActivity.class);
                        startActivity(jumpRegisterIntent);
                    }
                }).create().show();

    }
}
