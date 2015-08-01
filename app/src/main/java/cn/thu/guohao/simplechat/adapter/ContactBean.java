package cn.thu.guohao.simplechat.adapter;

/**
 * Created by Guohao on 2015/7/27.
 * Contact Item
 */
public class ContactBean {
    public String username;
    public String nickname;
    public String uri;
    public ContactBean(String username, String nickname, String uri) {
        this.username = username;
        this.nickname = nickname;
        this.uri = uri;
    }
}
