package cn.thu.guohao.simplechat.util;

/**
 * Created by Guohao on 2015/7/30.
 * Information Package
 */
public class InfoPack {
    public enum TYPE {ERROR, MESSAGE, MOMENTS, USER_UPDATE}
    private TYPE type;
    private String sender;
    private String content;
    private String uri;
    private String update_time;

    public static final String STR_MESSAGE = "message";
    public static final String STR_USER_UPDATE = "user_update";
    public static final String STR_MOMENTS = "moments";
    public static final String STR_ERROR = "error";

    public InfoPack() {
    }

    public InfoPack(TYPE type, String sender, String content, String uri, String update_time) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.uri = uri;
        this.update_time = update_time;
    }

    @Override
    public String toString() {
        return "InfoPack{" +
                "type=" + type +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", uri='" + uri + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}
