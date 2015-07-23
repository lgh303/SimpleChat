package cn.thu.guohao.simplechat;

/**
 * Created by Guohao on 2015/7/23.
 * Conversation Item in Chats Tab.
 */
public class ChatBean {
    public ChatBean(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public String title;
    public String content;
}
