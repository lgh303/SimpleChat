package cn.thu.guohao.simplechat.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Guohao on 2015/7/29.
 * Collection of Util functions
 */
public class Utils {

    public static String parseMessage(String jsonString) {
        String message;
        JSONTokener jsonTokener = new JSONTokener(jsonString);
        try {
            JSONObject obj = (JSONObject) jsonTokener.nextValue();
            message = obj.getString("content");
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
