package cn.thu.guohao.simplechat.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by Guohao on 2015/7/29.
 * Server Message ORM
 */
public class Message extends BmobObject {
    private Integer type; // 0: words; 1: picture
    private String username;
    private String content; // words
    private String uri;   // picture uri
    private Conversation conversation;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

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
