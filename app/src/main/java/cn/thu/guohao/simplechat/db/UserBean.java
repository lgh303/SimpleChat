package cn.thu.guohao.simplechat.db;

/**
 * Created by Guohao on 2015/7/27.
 * User Model for user.db
 */
public class UserBean {
    private String username;
    private String nickname;
    private int sex;
    private int type;
    private String photoUri;

    public UserBean() {
    }

    public UserBean(String username, String nickname, int sex, int type, String photoUri) {
        this.username = username;
        this.nickname = nickname;
        this.sex = sex;
        this.type = type;
        this.photoUri = photoUri;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", type=" + type +
                ", photoUri='" + photoUri + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
