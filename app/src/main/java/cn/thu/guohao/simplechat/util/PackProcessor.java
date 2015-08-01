package cn.thu.guohao.simplechat.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.ChatsDAO;
import cn.thu.guohao.simplechat.db.ConversationBean;
import cn.thu.guohao.simplechat.db.MessageBean;
import cn.thu.guohao.simplechat.db.MessageDAO;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.db.UserDAO;

/**
 * Created by Guohao on 2015/8/1.
 * Process Pack received from server.
 */
public class PackProcessor {
    private static PackProcessor processor;
    public static PackProcessor getInstance(Context context) {
        if (processor == null) {
            processor = new PackProcessor(context);
            return processor;
        }
        processor.context = context;
        return processor;
    }

    private Context context;
    private User mCurrUser;
    private UserDAO mUserDAO;
    private MessageDAO mMessageDAO;
    private ChatsDAO mChatsDAO;

    public static final String FORWARD_ACTION = "cn.thu.guohao.simplechat.FORWARD_ACTION";
    public static final String FORWARD_MESSAGE = "cn.thu.guohao.simplechat.FORWARD_MESSAGE";

    public PackProcessor(Context context) {
        this.context = context;
    }

    public void processPack(InfoPack pack) {
        if (pack.getType() != InfoPack.TYPE.ERROR) {
            mCurrUser = User.getCurrentUser(context, User.class);
            if (mCurrUser == null) return;
            if (pack.getType() == InfoPack.TYPE.MESSAGE) {
                mChatsDAO = new ChatsDAO(context, mCurrUser.getUsername());
                mMessageDAO = new MessageDAO(context, mCurrUser.getUsername());
                saveLocalUnread(pack);
                Intent forward_intent = new Intent();
                forward_intent.setAction(FORWARD_ACTION);
                forward_intent.putExtra(FORWARD_MESSAGE, pack);
                context.sendBroadcast(forward_intent);
            } else if (pack.getType() == InfoPack.TYPE.USER_UPDATE) {
                mUserDAO = new UserDAO(context, mCurrUser.getUsername());
                mChatsDAO = new ChatsDAO(context, mCurrUser.getUsername());
                updateLocalUser(context, pack);
                Log.i("lgh", "Update Local user Success");
            }
        }
    }

    private void saveLocalUnread(InfoPack pack) {

        int posType = 0, mediaType = 0;

        mMessageDAO.createMessageConvTable(pack.getSender());
        mMessageDAO.insertMessageToConvTable(
                pack.getSender(),
                new MessageBean(
                        posType, mediaType, pack.getSender(),
                        pack.getContent(), pack.getUri(),
                        pack.getUpdate_time()
                ),
                true
        );
        ConversationBean conv = mChatsDAO.getConversation(pack.getSender());
        mChatsDAO.updateConversation(
                pack.getSender(),
                null,
                pack.getContent(),
                pack.getUpdate_time(),
                conv.getUnreadCount() + 1,
                null
        );
    }

    private void updateLocalUser(Context context, InfoPack pack) {
        UserBean user = mUserDAO.get(pack.getSender());
        user.setNickname(pack.getContent());
        if (!user.getPhotoUri().equals(pack.getUri())) {
            user.setPhotoUri(pack.getUri());
            if (!BitmapLoader.hasNoCache())
                BitmapLoader.getInstance(context).removeBitmapFromCache(user.getUsername());
            Log.i("lgh", "Received Photo Update Message");
        }
        int sex = 0;
        if (pack.getUpdate_time().equals(context.getString(R.string.hint_male)))
            sex = 1;
        user.setSex(sex);
        mUserDAO.update(user);

        mChatsDAO.updateConversation(
                pack.getSender(),
                pack.getContent(),
                null,
                null,
                null,
                pack.getUri()
        );
    }
}
