package cn.thu.guohao.simplechat.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import cn.thu.guohao.simplechat.adapter.ChatItemBean;

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
            switch (type) {
                case InfoPack.STR_MESSAGE:
                    return new InfoPack(InfoPack.TYPE.MESSAGE, sender, content, uri, update_time);
                case InfoPack.STR_USER_UPDATE:
                    return new InfoPack(InfoPack.TYPE.USER_UPDATE, sender, content, uri, update_time);
                case InfoPack.STR_MOMENTS:
                    return new InfoPack(InfoPack.TYPE.MOMENTS, sender, content, uri, update_time);
                case InfoPack.STR_ERROR:
                    return new InfoPack(InfoPack.TYPE.ERROR, null, null, null, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new InfoPack(InfoPack.TYPE.ERROR, null, null, null, null);
    }
}
