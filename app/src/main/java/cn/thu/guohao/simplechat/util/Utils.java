package cn.thu.guohao.simplechat.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import cn.thu.guohao.simplechat.adapter.ChatItemBean;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.UserBean;

/**
 * Created by Guohao on 2015/7/29.
 * Collection of Util functions
 */
public class Utils {

    public static String makeJsonString(String type, String sender, String content, String uri, String update_time) {
        return "{" +
                "\"type\":" + "\"" + type + "\"" + "," +
                "\"sender\":" + "\"" + sender + "\"" + "," +
                "\"content\":" + "\"" + content + "\"" + "," +
                "\"uri\":" + "\"" + uri + "\"" + "," +
                "\"update_time\":" + "\"" + update_time + "\"" +
                "}";
    }

    public static InfoPack parseMessage(String jsonString) {
        JSONTokener jsonTokener = new JSONTokener(jsonString);
        try {
            JSONObject obj = (JSONObject) jsonTokener.nextValue();
            String type, sender, content, uri, update_time;
            type = obj.getString("type");
            sender = obj.getString("sender");
            content = obj.getString("content");
            uri = obj.getString("uri");
            update_time = obj.getString("update_time");
            InfoPack.TYPE infoType = InfoPack.TYPE.ERROR;
            switch (type) {
                case InfoPack.STR_MESSAGE:
                    infoType = InfoPack.TYPE.MESSAGE;
                    break;
                case InfoPack.STR_USER_UPDATE:
                    infoType = InfoPack.TYPE.USER_UPDATE;
                    break;
                case InfoPack.STR_INVITE:
                    infoType = InfoPack.TYPE.INVITE;
                    break;
                case InfoPack.STR_ACCEPT:
                    infoType = InfoPack.TYPE.ACCEPT;
                    break;
                case InfoPack.STR_MOMENTS:
                    infoType = InfoPack.TYPE.MOMENTS;
                    break;
                case InfoPack.STR_ERROR:
                    return new InfoPack(InfoPack.TYPE.ERROR, null, null, null, null);
                default:
                    Log.i("lgh", "Cannot parse InfoPack type");
            }
            return new InfoPack(infoType, sender, content, uri, update_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new InfoPack(InfoPack.TYPE.ERROR, null, null, null, null);
    }

    public static void hideKeyboard(Activity context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(context.findViewById(android.R.id.content).getWindowToken(), 0);
    }

    public static UserBean user2bean(User user) {
        int sex = 0;
        if (user.getIsMale()) sex = 1;
        return new UserBean(
                user.getUsername(),
                user.getNickname(),
                sex, 0,
                user.getPhotoUri()
        );
    }
}
