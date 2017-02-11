package cn.jinmiao.bbs.perfectstoryapp.utils;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fairy on 2016/1/9.
 *
 *  活动管理类
 */
public class AppCmpaActivityCollector {

    public static List<AppCompatActivity> appCompatActivities = new ArrayList<AppCompatActivity>();

    public static void addAppCompatActivity(AppCompatActivity activity) {
        appCompatActivities.add(activity);
    }

    public static void removeAppCompatActivity(AppCompatActivity activity) {
        appCompatActivities.remove(activity);
    }

    public static void finishAll() {
        for (AppCompatActivity appCompatActivity : appCompatActivities) {
            if (!appCompatActivity.isFinishing()) {
                appCompatActivity.finish();
            }
        }
    }
}
