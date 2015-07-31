package cn.thu.guohao.simplechat.adapter;

/**
 * Created by Guohao on 2015/7/23.
 * Conversation Item in Chats Tab.
 */
public class ChatBean {
    public ChatBean(String convID, String username, String title, String content, int unread, String time, String uri) {
        this.convID = convID;
        this.username = username;
        this.title = title;
        this.content = content;
        this.unread = unread;
        this.time = time;
        this.uri = uri;
    }
    public String convID;
    public String username;
    public String title;
    public String content;
    public int unread;
    public String time;
    public String uri;
}
