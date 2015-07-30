package cn.thu.guohao.simplechat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Guohao on 2015/7/28.
 * Chats DB Helper
 */
public class ChatsDBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String CREATE_CONV_SQL = "create table conversation(" +
            "_id integer primary key autoincrement," +
            "id text," +
            "title text," +
            "unread_count text," +
            "friend_username text," +
            "latest_message text," +
            "update_time text)";
    private static final String DROP_CONV_SQL = "drop table if exists conversation";

    public ChatsDBHelper(Context context, String name) {
        super(context, name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONV_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_CONV_SQL);
        db.execSQL(CREATE_CONV_SQL);
    }
}
