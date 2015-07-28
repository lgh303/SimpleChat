package cn.thu.guohao.simplechat.db;


import cn.thu.guohao.simplechat.data.Conversation;

/**
 * Created by Guohao on 2015/7/28.
 * Conversation for chats.db
 */
public class ConversationBean {
    private String title;
    private String friend_username;
    private String latestMessage;
    private String update_time;

    public ConversationBean() {}

    public ConversationBean(String title, String friend_username, String latestMessage, String update_time) {
        this.title = title;
        this.friend_username = friend_username;
        this.latestMessage = latestMessage;
        this.update_time = update_time;
    }

    @Override
    public String toString() {
        return "ConversationBean{" +
                "title='" + title + '\'' +
                ", friend_username='" + friend_username + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFriend_username() {
        return friend_username;
    }

    public void setFriend_username(String friend_username) {
        this.friend_username = friend_username;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}