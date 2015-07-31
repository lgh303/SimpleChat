package cn.thu.guohao.simplechat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Guohao on 2015/7/27.
 * SQLite user.db helper
 */
public class UserDBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String USER_CREATE = "create table user(" +
            "_id integer primary key autoincrement," +
            "username text," +
            "nickname text," +
            "sex integer," +
            "type integer," +
            "photo_uri text)";
    private static final String USER_DROP = "drop table if exists user";

    public UserDBHelper(Context context, String DBName) {
        super(context, DBName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(USER_DROP);
        db.execSQL(USER_CREATE);
    }
}
