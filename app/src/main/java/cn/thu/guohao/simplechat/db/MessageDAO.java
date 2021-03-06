package cn.thu.guohao.simplechat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Guohao on 2015/7/28.
 * Message Database Access Object
 */
public class MessageDAO {
    private MessageDBHelper helper;
    private String DBName;

    public MessageDAO(Context context, String prefix) {
        DBName = prefix + "_message.db";
        helper = new MessageDBHelper(context, DBName);
    }

    public void createMessageConvTable(String friend_username) {
        String table_name = "message_conversation_" + friend_username;
        String createSQL = "create table if not exists " + table_name + "(" +
                "_id integer primary key autoincrement," +
                "pos_type integer," +
                "media_type integer," +
                "speaker text," +
                "content text," +
                "uri text," +
                "update_time text) ";
        String createSQL_unread = "create table if not exists " +
                table_name + "_unread" + "(" +
                "_id integer primary key autoincrement," +
                "pos_type integer," +
                "media_type integer," +
                "speaker text," +
                "content text," +
                "uri text," +
                "update_time text) ";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(createSQL);
        db.execSQL(createSQL_unread);
        db.close();
    }

    public void dropMessageConvTable(String friend_username) {
        String table_name = "message_conversation_" + friend_username;
        String dropSQL = "drop table if exists" + table_name;
        String dropSQL_unread = "drop table if exists" + table_name + "_unread";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(dropSQL);
        db.execSQL(dropSQL_unread);
        db.close();
    }

    public void insertMessageToConvTable(String friend_username, MessageBean m, boolean unread) {
        String table_name = "message_conversation_" + friend_username;
        if (unread) table_name += "_unread";
        String insertSQL = "insert into " + table_name + "( " +
                "pos_type, media_type, speaker, content, uri, update_time) " +
                "values(?,?,?,?,?,?)";
        Object[] params = new Object[] {
                m.getPosType(), m.getMediaType(), m.getSpeaker(),
                m.getContent(), m.getUri(), m.getUpdate_time()
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(insertSQL, params);
        db.close();
    }

    public void markMessageReadInConvTable(String friend_username) {
        String table_name = "message_conversation_" + friend_username;
        String table_name_unread = table_name + "_unread";
        ArrayList<MessageBean> messages = getMessageFromConvTable(friend_username, true);
        for (MessageBean message : messages)
            insertMessageToConvTable(friend_username, message, false);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " + table_name_unread);
        db.close();
    }

    public ArrayList<MessageBean> getMessageFromConvTable(String friend_username, boolean unread) {
        String table_name = "message_conversation_" + friend_username;
        if (unread) table_name += "_unread";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(table_name, null, null, null, null, null, null, null);
        ArrayList<MessageBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            MessageBean message = new MessageBean();
            message.setPosType(cursor.getInt(cursor.getColumnIndex("pos_type")));
            message.setMediaType(cursor.getInt(cursor.getColumnIndex("media_type")));
            message.setSpeaker(cursor.getString(cursor.getColumnIndex("speaker")));
            message.setContent(cursor.getString(cursor.getColumnIndex("content")));
            message.setUri(cursor.getString(cursor.getColumnIndex("uri")));
            message.setUpdate_time(cursor.getString(cursor.getColumnIndex("update_time")));
            list.add(message);
        }
        cursor.close();
        db.close();
        return list;
    }
}
