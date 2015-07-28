package cn.thu.guohao.simplechat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Guohao on 2015/7/28.
 * Message.db helper
 */
public class MessageDBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private ArrayList<String> tables;

    public MessageDBHelper(Context context, String name) {
        super(context, name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        tables = new ArrayList<>();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
