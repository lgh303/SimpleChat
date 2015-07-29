package cn.thu.guohao.simplechat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Guohao on 2015/7/28.
 * chats Database Access Object
 */
public class ChatsDAO {
    private ChatsDBHelper helper;
    private String mDBName;

    public ChatsDAO(Context context, String prefix) {
        mDBName = prefix + "_chats.db";
        helper = new ChatsDBHelper(context, mDBName);
    }

    public void insertConversation(ConversationBean conv) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String insertSQL = "insert into conversation( " +
                "id, title, friend_username, latest_message, update_time) " +
                "values(?,?,?,?,?)";
        Object[] params = new Object[] {
                conv.getId(),
                conv.getTitle(), conv.getFriend_username(),
                conv.getLatestMessage(), conv.getUpdate_time()
        };
        db.execSQL(insertSQL, params);
        db.close();
    }

    public void deleteConversation(ConversationBean conv) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String deleteSQL = "delete from conversation where friend_username=" +
                conv.getFriend_username();
        db.execSQL(deleteSQL);
        db.close();
    }

    public ConversationBean getConversation(String friend_username) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String selectSQL = "select * from conversation where friend_username=?";
        Cursor cursor = db.rawQuery(selectSQL, new String[]{friend_username});
        ConversationBean conv = null;
        if (cursor.moveToNext()) {
            conv = new ConversationBean();
            conv.setId(cursor.getString(cursor.getColumnIndex("id")));
            conv.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            conv.setFriend_username(cursor.getString(cursor.getColumnIndex("friend_username")));
            conv.setLatestMessage(cursor.getString(cursor.getColumnIndex("latest_message")));
            conv.setUpdate_time(cursor.getString(cursor.getColumnIndex("update_time")));
        }
        cursor.close();
        db.close();
        return conv;
    }

    public ArrayList<ConversationBean> getConversation() {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("conversation", null, null, null, null, null, null, null);
        ArrayList<ConversationBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {

            ConversationBean conv = new ConversationBean();
            conv.setId(cursor.getString(cursor.getColumnIndex("id")));
            conv.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            conv.setFriend_username(cursor.getString(cursor.getColumnIndex("friend_username")));
            conv.setLatestMessage(cursor.getString(cursor.getColumnIndex("latest_message")));
            conv.setUpdate_time(cursor.getString(cursor.getColumnIndex("update_time")));
            list.add(conv);
        }
        cursor.close();
        db.close();
        return list;
    }
}
