package cn.jinmiao.bbs.perfectstoryapp.fragment.usercenter.modifypwd;

import android.content.SharedPreferences;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.ModifyResultBean;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyHttpCallbackListenerUtil;
import cn.jinmiao.bbs.perfectstoryapp.utils.MyPostHttpUtil;

public class ModifyPasswordActivity extends AppCompatActivity implements OnClickListener{

    private ImageView comeBackImageView;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText reNewPasswordEditText;
    private Button modifyPasswordBtn;

    private String modifyResultStrJSONData;

    private ModifyResultBean modifyResultBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        findViewById();


    }

    private void findViewById() {

        comeBackImageView=(ImageView)findViewById(R.id.comeback_modify_ImageView_id);
        comeBackImageView.setOnClickListener(this);
        oldPasswordEditText=(EditText)findViewById(R.id.old_password_EditText_id);
        newPasswordEditText=(EditText)findViewById(R.id.new_password_EditText_id);
        reNewPasswordEditText=(EditText)findViewById(R.id.re_new_password_EditText_id);
        modifyPasswordBtn=(Button)findViewById(R.id.modify_password_btn_id);
        modifyPasswordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.modify_password_btn_id:
                checkModifyIsOKMethod();
                break;
            case R.id.comeback_modify_ImageView_id:
                ModifyPasswordActivity.this.finish();
                break;
            default: break;
        }

    }

    private boolean checkModifyIsOKMethod() {
        if(oldPasswordEditText.getText().toString().trim().equals(""))
        {
            Toast.makeText(ModifyPasswordActivity.this,"原始密码不能为空",Toast.LENGTH_SHORT).show();
            return  false;
        }else if(newPasswordEditText.getText().toString().trim().equals("")){
             Toast.makeText(ModifyPasswordActivity.this,"新密码不能为空",Toast.LENGTH_SHORT).show();
             return  false;
        }
        if(!newPasswordEditText.getText().toString().trim().equals(reNewPasswordEditText.getText().toString().trim()))
        {
            Toast.makeText(ModifyPasswordActivity.this,"两次密码输入不一致,请重新输入",Toast.LENGTH_SHORT).show();
            return false;
        }
        SharedPreferences pref = ModifyPasswordActivity.this.getSharedPreferences("TempUserUIdFile", ModifyPasswordActivity.this.MODE_PRIVATE);
        int userUid = pref.getInt("tempUserUid",-1);

        if(userUid==-1)
        {
            Toast.makeText(ModifyPasswordActivity.this,"用户数据被非法破坏,请注销重新登陆",Toast.LENGTH_SHORT).show();
            return false;
        }

        MyPostHttpUtil.myPostHttpRequestUtils("http://122.114.58.68:8066/AppService/UserService.ashx?action=changePassword", "&OldPs=" + oldPasswordEditText.getText().toString().trim() + "&Uid=" + userUid + "&NewPs=" + newPasswordEditText.getText().toString().trim(), new MyHttpCallbackListenerUtil() {
            @Override
            public void onFinish(String response) {
                   modifyResultStrJSONData=response;
                   modifyPasswordHandler.sendEmptyMessage(1);
            }

            @Override
            public void onError(Exception e) {
                   modifyPasswordHandler.sendEmptyMessage(2);
            }
        });

        return true;
    }

   private android.os.Handler modifyPasswordHandler=new android.os.Handler(){

       @Override
       public void handleMessage(Message msg) {
          switch (msg.what)
          {
              case 1:

                  try {
                        Gson modifyPwdGson = new Gson();
                        modifyResultBean=modifyPwdGson.fromJson(modifyResultStrJSONData, ModifyResultBean.class);
                  } catch (Exception e) {
                      Toast.makeText(ModifyPasswordActivity.this,"数据解析异常,请稍后重试！",Toast.LENGTH_SHORT).show();
                  }
                  if(modifyResultBean.getChangePassword().trim().equals("OldPsError"))
                  {
                      Toast.makeText(ModifyPasswordActivity.this,"修改失败,原始密码不正确",Toast.LENGTH_SHORT).show();
                  }else if(modifyResultBean.getChangePassword().trim().equals("error"))
                  {
                      Toast.makeText(ModifyPasswordActivity.this,"密码修改失败",Toast.LENGTH_SHORT).show();
                  }else if(modifyResultBean.getChangePassword().trim().equals("Success")){
                      Toast.makeText(ModifyPasswordActivity.this,"恭喜您,密码修改成功",Toast.LENGTH_SHORT).show();
                      ModifyPasswordActivity.this.finish();
                  }
                  break;
              case 2:
                  Toast.makeText(ModifyPasswordActivity.this,"服务器连接失败,请检查网络设置!",Toast.LENGTH_SHORT).show();
                  break;
              default:
                  break;
          }
       }
   };
}
