package cn.thu.guohao.simplechat.db;

/**
 * Created by Guohao on 2015/7/28.
 * Message for message.db
 */
public class MessageBean {
    private int posType;
    private int mediaType;
    private String speaker;
    private String content;
    private String uri;
    private String update_time;

    public MessageBean() {
    }

    public MessageBean(int posType, int mediaType, String speaker, String content, String uri, String update_time) {
        this.posType = posType;
        this.mediaType = mediaType;
        this.speaker = speaker;
        this.content = content;
        this.uri = uri;
        this.update_time = update_time;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "posType=" + posType +
                ", mediaType=" + mediaType +
                ", speaker='" + speaker + '\'' +
                ", content='" + content + '\'' +
                ", uri='" + uri + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }

    public int getPosType() {
        return posType;
    }

    public void setPosType(int posType) {
        this.posType = posType;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
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
