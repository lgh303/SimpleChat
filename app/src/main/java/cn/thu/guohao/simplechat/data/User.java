package cn.thu.guohao.simplechat.data;

import android.util.Log;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Guohao on 2015/7/25.
 * User
 */
public class User extends BmobUser {
    private String nickname;
    private Boolean isMale;
    private String photoUri;
    private BmobRelation friends;
    private BmobRelation conversations;

    public BmobRelation getConversations() {
        return conversations;
    }

    public void setConversations(BmobRelation conversations) {
        this.conversations = conversations;
    }

    public BmobRelation getFriends() {
        return friends;
    }

    public void setFriends(BmobRelation friends) {
        this.friends = friends;
    }

    public Boolean getIsMale() {
        return isMale;
    }

    public void setIsMale(Boolean isMale) {
        this.isMale = isMale;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
