package cn.thu.guohao.simplechat.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by Guohao on 2015/8/1.
 * Delivery Json waits to send. Saved in server
 */
public class Delivery extends BmobObject {
    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
