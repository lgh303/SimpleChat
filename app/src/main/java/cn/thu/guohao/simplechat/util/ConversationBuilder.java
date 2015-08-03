package cn.thu.guohao.simplechat.util;

import android.content.Context;

import cn.bmob.v3.listener.SaveListener;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.Conversation;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.ChatsDAO;
import cn.thu.guohao.simplechat.db.ConversationBean;
import cn.thu.guohao.simplechat.db.MessageBean;
import cn.thu.guohao.simplechat.db.MessageDAO;

/**
 * Created by Guohao on 2015/8/2.
 * Build Conversation in remote & local storage.
 */
public class ConversationBuilder {

    private Context context;
    private ChatsDAO mChatsDAO;
    private MessageDAO mMessageDAO;

    public ConversationBuilder(Context context, ChatsDAO mChatsDAO, MessageDAO mMessageDAO) {
        this.context = context;
        this.mChatsDAO = mChatsDAO;
        this.mMessageDAO = mMessageDAO;
    }

    public void setConversation(Conversation conv, User aUser, User bUser) {
        conv.setaUser(aUser);
        conv.setaUsername(aUser.getUsername());
        conv.setaNickname(aUser.getNickname());
        conv.setaUri(aUser.getPhotoUri());
        conv.setbUser(bUser);
        conv.setbUsername(bUser.getUsername());
        conv.setbNickname(bUser.getNickname());
        conv.setbUri(bUser.getPhotoUri());
        conv.setUnread(1);
        conv.setLatestMessage(context.getString(R.string.chat_first_message));
    }

    public void updateLocal(Conversation conv) {
        mChatsDAO.insertConversation(new ConversationBean(
                conv.getObjectId(),
                conv.getbNickname(),
                conv.getbUsername(),
                0,
                conv.getLatestMessage(),
                conv.getCreatedAt(),
                conv.getbUri()));
        mMessageDAO.createMessageConvTable(conv.getbUsername());
        mMessageDAO.insertMessageToConvTable(
                conv.getbUsername(),
                new MessageBean(
                        2, 0, "Notification",
                        context.getString(R.string.chat_first_message),
                        null, conv.getUpdatedAt()
                ),
                true
        );
        mChatsDAO.updateConversation(
                conv.getbUsername(),
                conv.getbNickname(),
                context.getString(R.string.chat_first_message),
                conv.getCreatedAt(),
                1,
                conv.getbUri()
        );
    }
}
