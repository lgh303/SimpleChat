package cn.thu.guohao.simplechat.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by Guohao on 2015/7/29.
 * Collection of Util functions
 */
public class Utils {

    public static String[] parseMessage(String jsonString) {
        String username, message;
        JSONTokener jsonTokener = new JSONTokener(jsonString);
        try {
            JSONObject obj = (JSONObject) jsonTokener.nextValue();
            username = obj.getString("user");
            message = obj.getString("content");
            return new String[] {username, message};
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
