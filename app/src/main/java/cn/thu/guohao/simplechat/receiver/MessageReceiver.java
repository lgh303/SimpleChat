package cn.thu.guohao.simplechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.bmob.push.PushConstants;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.ChatsDAO;
import cn.thu.guohao.simplechat.db.ConversationBean;
import cn.thu.guohao.simplechat.db.MessageBean;
import cn.thu.guohao.simplechat.db.MessageDAO;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.db.UserDAO;
import cn.thu.guohao.simplechat.util.InfoPack;
import cn.thu.guohao.simplechat.util.Utils;

/**
 * Created by Guohao on 2015/7/30.
 * Receive message from cloud.
 */
public class MessageReceiver extends BroadcastReceiver {

    private User mCurrUser;
    private UserDAO mUserDAO;
    private MessageDAO mMessageDAO;
    private ChatsDAO mChatsDAO;

    public static final String FORWARD_ACTION = "cn.thu.guohao.simplechat.FORWARD_ACTION";
    public static final String FORWARD_MESSAGE = "cn.thu.guohao.simplechat.FORWARD_MESSAGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String jsonString = intent.getStringExtra(
                    PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            InfoPack pack = Utils.parseMessage(jsonString);
            Log.i("lgh", "InfoPack: " + pack.toString());
            if (pack.getType() != InfoPack.TYPE.ERROR) {
                mCurrUser = User.getCurrentUser(context, User.class);
                if (mCurrUser == null) return;
                if (pack.getType() == InfoPack.TYPE.MESSAGE) {
                    mChatsDAO = new ChatsDAO(context, mCurrUser.getUsername());
                    mMessageDAO = new MessageDAO(context, mCurrUser.getUsername());
                    saveLocalUnread(pack);
                    Intent forward_intent = new Intent();
                    forward_intent.setAction(FORWARD_ACTION);
                    forward_intent.putExtra(FORWARD_MESSAGE, jsonString);
                    context.sendBroadcast(forward_intent);
                } else if (pack.getType() == InfoPack.TYPE.USER_UPDATE) {
                    mUserDAO = new UserDAO(context, mCurrUser.getUsername());
                    mChatsDAO = new ChatsDAO(context, mCurrUser.getUsername());
                    updateLocalUser(context, pack);
                    Log.i("lgh", "Update Local user Success");
                }
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
                conv.getUnreadCount() + 1
        );
    }

    private void updateLocalUser(Context context, InfoPack pack) {
        UserBean user = mUserDAO.get(pack.getSender());
        user.setNickname(pack.getContent());
        if (!user.getPhotoUri().equals(pack.getUri())) {
            user.setPhotoUri(pack.getUri());
            Log.i("lgh", "Received Photo Update Message");
            // TODO download new photo
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
            null
        );
    }
}
