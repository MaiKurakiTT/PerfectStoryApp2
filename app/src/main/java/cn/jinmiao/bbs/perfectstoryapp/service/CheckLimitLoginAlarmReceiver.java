package cn.jinmiao.bbs.perfectstoryapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zhaqingf on 2016/3/17.
 */
public class CheckLimitLoginAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //构建出了一个 Intent 对象，然后去启动这个服务
        Intent i = new Intent(context, LoginLimitCheckService.class);
        context.startService(i);
    }
}
