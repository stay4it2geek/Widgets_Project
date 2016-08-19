package cn.com.custom.widgetproject.widget;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 历史检索数据库帮助类
 * Created by custom on 2016/6/20.
 */
public class HistorySearchDBhelper extends SQLiteOpenHelper {

    private static HistorySearchDBhelper dbHelper = null;
    public static final String TABLE_NAME="historySearch";//表名
    public static final String WORDS_HISTORY="wordsHistory";//字段（列名）
    public static final String KEY_ID="_id";//id名

    public static HistorySearchDBhelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new HistorySearchDBhelper(context);
        }
         return dbHelper;
    }

    private HistorySearchDBhelper(Context context) {
        super(context, "db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ("+KEY_ID+" INTEGER PRIMARY KEY  AUTOINCREMENT, "+ WORDS_HISTORY +" TEXT )";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
