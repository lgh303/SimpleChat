package cn.thu.guohao.simplechat.db;


/**
 * Created by Guohao on 2015/7/28.
 * Conversation for chats.db
 */
public class ConversationBean {
    private String id;
    private String title;
    private String friend_username;
    private int unreadCount;
    private String latestMessage;
    private String update_time;

    public ConversationBean() {}

    public ConversationBean(String id, String title, String friend_username, int unreadCount, String latestMessage, String update_time) {
        this.id = id;
        this.title = title;
        this.friend_username = friend_username;
        this.unreadCount = unreadCount;
        this.latestMessage = latestMessage;
        this.update_time = update_time;
    }

    @Override
    public String toString() {
        return "ConversationBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", friend_username='" + friend_username + '\'' +
                ", unreadCount=" + unreadCount +
                ", latestMessage='" + latestMessage + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
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
