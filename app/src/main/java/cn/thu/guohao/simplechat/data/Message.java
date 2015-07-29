package cn.thu.guohao.simplechat.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by Guohao on 2015/7/29.
 * Server Message ORM
 */
public class Message extends BmobObject {
    private String username;
    private String content;
    private Conversation conversation;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
