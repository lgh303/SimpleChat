package cn.thu.guohao.simplechat.adapter;

/**
 * Created by Guohao on 2015/7/23.
 * Conversation Item in Chats Tab.
 */
public class ChatBean {
    public ChatBean(String convID, String username, String title, String content) {
        this.convID = convID;
        this.username = username;
        this.title = title;
        this.content = content;
    }
    public String convID;
    public String username;
    public String title;
    public String content;
}
