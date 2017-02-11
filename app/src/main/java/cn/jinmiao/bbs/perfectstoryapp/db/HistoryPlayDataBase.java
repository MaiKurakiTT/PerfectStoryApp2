package cn.jinmiao.bbs.perfectstoryapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.database.sqlite.SQLiteDatabase.*;

/**
 * Created by fairy on 2016/2/3.
 * 播放信息缓存数据库
 */
public class HistoryPlayDataBase extends SQLiteOpenHelper {
    /**
     * uid 自增长字段
     * history_music_id 播放歌曲Id
     *
     */
    public static final String CREATE_HISTORY_TABLE = "create table t_history_music("
            + "uid integer primary key autoincrement, "
            + "history_music_id integer, "
            + "history_music_subject text, "
            + "history_music_path text) ";
    private Context mContext;

    public HistoryPlayDataBase(Context context, String name,
                                 CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //创建数据库表
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }





}
