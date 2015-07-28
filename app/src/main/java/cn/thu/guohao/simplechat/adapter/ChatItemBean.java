package cn.thu.guohao.simplechat.adapter;

/**
 * Created by Guohao on 2015/7/24.
 * Item in Chat Activity
 */
public class ChatItemBean {
    public enum TYPE {LEFT, RIGHT, MIDDLE};
    public String text;
    public TYPE type;

    public ChatItemBean(String text, TYPE type) {
        this.text = text;
        this.type = type;
    }
}
