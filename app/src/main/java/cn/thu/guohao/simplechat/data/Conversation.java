package cn.thu.guohao.simplechat.data;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Guohao on 2015/7/28.
 * Server Conversation ORM
 */
public class Conversation extends BmobObject {
    private User aUser;
    private User bUser;
    private BmobRelation messages;
    private String latestMessage;
    private String aNickname;
    private String bNickname;

    public String getaNickname() {
        return aNickname;
    }

    public void setaNickname(String aNickname) {
        this.aNickname = aNickname;
    }

    public String getbNickname() {
        return bNickname;
    }

    public void setbNickname(String bNickname) {
        this.bNickname = bNickname;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public User getaUser() {
        return aUser;
    }

    public void setaUser(User aUser) {
        this.aUser = aUser;
    }

    public User getbUser() {
        return bUser;
    }

    public void setbUser(User bUser) {
        this.bUser = bUser;
    }

    public BmobRelation getMessages() {
        return messages;
    }

    public void setMessages(BmobRelation messages) {
        this.messages = messages;
    }
}