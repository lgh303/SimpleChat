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
                case "notification":
                    return new InfoPack(InfoPack.TYPE.NOTIFICATION, sender, content, null, update_time);
                case "message":
                    return new InfoPack(InfoPack.TYPE.MESSAGE, sender, content, uri, update_time);
                case "moments":
                    return new InfoPack(InfoPack.TYPE.MOMENTS, sender, content, uri, update_time);
                case "error":
                    return new InfoPack(InfoPack.TYPE.ERROR, null, null, null, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new InfoPack(InfoPack.TYPE.ERROR, null, null, null, null);
    }
}
