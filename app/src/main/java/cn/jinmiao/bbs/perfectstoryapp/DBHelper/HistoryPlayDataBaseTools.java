package cn.jinmiao.bbs.perfectstoryapp.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.bean.MP3Info;
import cn.jinmiao.bbs.perfectstoryapp.db.HistoryPlayDataBase;

/**
 * Created by zhaqingf on 2016/2/28.
 */
public class HistoryPlayDataBaseTools {

    private Context mContext;
    private SQLiteDatabase historyMusicDB;
    private ContentValues historyMusicContentValues;

    private  List<MP3Info> mp3InfoList=new ArrayList<MP3Info>();

    public void addHistoryMusic(Context mContext,MP3Info mp3Info){
        //打开数据库
        HistoryPlayDataBase historyMusicDBHelper=new HistoryPlayDataBase(mContext,"HistoryMusic.db",null,1);
        SQLiteDatabase db = historyMusicDBHelper.getReadableDatabase();

        Cursor mCursor=null;

        historyMusicDB.beginTransaction();

        try {
            mCursor = historyMusicDB.rawQuery(
                    "select * from t_history_music where history_music_id=?",
                    new String[] { (mp3Info.getId() + "").trim() });

            if(!mCursor.moveToFirst())//数据库没有查询到数据
            {
                historyMusicContentValues = new ContentValues();
                historyMusicContentValues.put("history_music_id",mp3Info.getId());
                historyMusicContentValues.put("history_music_subject",mp3Info.getSubject());
                historyMusicContentValues.put("history_music_path",mp3Info.getMp3_path());

                historyMusicDB.insert("t_history_music",null,historyMusicContentValues);
                historyMusicDB.setTransactionSuccessful();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            mCursor.close();
            historyMusicDB.endTransaction();
            historyMusicDB.close();
            Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
        }

    }

    public List<MP3Info> findAllHistoryMusicList(Context mContext){

        //打开数据库
        HistoryPlayDataBase historyMusicDBHelper=new HistoryPlayDataBase(mContext,"HistoryMusic.db",null,1);
        SQLiteDatabase db = historyMusicDBHelper.getReadableDatabase();

        Cursor cursor = db.query("t_history_music", null, null, null, null, null, null);

        //开始查询
        if(cursor.moveToFirst()){
            do {
                MP3Info historyMP3=new MP3Info();
                historyMP3.setId(cursor.getString(cursor.getColumnIndex("history_music_id")));
                historyMP3.setSubject(cursor.getString(cursor.getColumnIndex("history_music_subject")));
                historyMP3.setMp3_path(cursor.getString(cursor.getColumnIndex("history_music_path")));
                mp3InfoList.add(historyMP3);
            } while (cursor.moveToNext());
        }
        return mp3InfoList;
    }
}
