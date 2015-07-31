package cn.thu.guohao.simplechat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Guohao on 2015/7/27.
 * user.db Database Access Object
 */
public class UserDAO {
    private UserDBHelper helper;
    private String DBName;
    public UserDAO(Context context, String prefix) {
        DBName = prefix + "_user.db";
        helper = new UserDBHelper(context, DBName);
    }

    public void insert(UserBean user) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String insertSQL = "insert into user( " +
                "username, nickname, sex, type, photo_uri) " +
                "values(?,?,?,?,?)";
        Object[] params = new Object[] {
                user.getUsername(), user.getNickname(),
                user.getSex(), user.getType(), user.getPhotoUri()
        };
        db.execSQL(insertSQL, params);
        db.close();
    }

    public void update(UserBean user) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put("nickname", user.getNickname());
        data.put("sex", user.getSex());
        data.put("type", user.getType());
        data.put("photo_uri", user.getPhotoUri());
        db.update("user", data,
                "username=?", new String[]{user.getUsername()});
        db.close();
    }

    public void delete(UserBean user) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String deleteSQL = "delete from user where username=" + user.getUsername();
        db.execSQL(deleteSQL);
        db.close();
    }

    public UserBean get(String username) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String selectSQL = "select * from user where username=?";
        Cursor cursor = db.rawQuery(selectSQL, new String[]{username});
        UserBean user = null;
        if (cursor.moveToNext()) {
            user = new UserBean();
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
            user.setSex(cursor.getInt(cursor.getColumnIndex("sex")));
            user.setType(cursor.getInt(cursor.getColumnIndex("type")));
            user.setPhotoUri(cursor.getString(cursor.getColumnIndex("photo_uri")));
        }
        cursor.close();
        db.close();
        return user;
    }

    public ArrayList<UserBean> get() {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null, null);
        ArrayList<UserBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            UserBean user = new UserBean();
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
            user.setSex(cursor.getInt(cursor.getColumnIndex("sex")));
            user.setType(cursor.getInt(cursor.getColumnIndex("type")));
            user.setPhotoUri(cursor.getString(cursor.getColumnIndex("photo_uri")));
            list.add(user);
        }
        cursor.close();
        db.close();
        return list;
    }
}
