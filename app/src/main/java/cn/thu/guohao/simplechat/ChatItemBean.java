package cn.thu.guohao.simplechat;

/**
 * Created by Guohao on 2015/7/24.
 */
public class ChatItemBean {
    public enum TYPE {LEFT, RIGHT};
    public String text;
    public TYPE type;

    public ChatItemBean(String text, TYPE type) {
        this.text = text;
        this.type = type;
    }
}
