package cn.thu.guohao.simplechat.data;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by Guohao on 2015/7/29.
 * Server Installation ORM
 */
public class Installation extends BmobInstallation {

    private String username;

    public Installation(Context context) {
        super(context);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
