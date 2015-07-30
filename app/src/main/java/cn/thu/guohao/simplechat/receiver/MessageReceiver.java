package cn.thu.guohao.simplechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.bmob.push.PushConstants;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.ChatsDAO;
import cn.thu.guohao.simplechat.db.MessageBean;
import cn.thu.guohao.simplechat.db.MessageDAO;
import cn.thu.guohao.simplechat.util.InfoPack;
import cn.thu.guohao.simplechat.util.Utils;

/**
 * Created by Guohao on 2015/7/30.
 * Receive message from cloud.
 */
public class MessageReceiver extends BroadcastReceiver {

    private User mCurrUser;
    private MessageDAO mMessageDAO;
    private ChatsDAO mChatsDAO;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String jsonString = intent.getStringExtra(
                    PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            InfoPack pack = Utils.parseMessage(jsonString);
            if (pack.getType() != InfoPack.TYPE.ERROR) {
                mCurrUser = User.getCurrentUser(context, User.class);
                if (mCurrUser == null) return;
                mMessageDAO = new MessageDAO(context, mCurrUser.getUsername());
                mChatsDAO = new ChatsDAO(context, mCurrUser.getUsername());
                saveLocalUnread(pack);
            }
        }
    }

    private void saveLocalUnread(InfoPack pack) {

        int posType = 0, mediaType = 0;
        if (pack.getType() == InfoPack.TYPE.NOTIFICATION)
            posType = 2;
        mMessageDAO.insertMessageToConvTable(
                pack.getSender(),
                new MessageBean(
                        posType, mediaType, pack.getSender(),
                        pack.getContent(), pack.getUri(),
                        pack.getUpdate_time()
                )
        );
        mChatsDAO.updateConversation(
                pack.getSender(),
                pack.getContent(),
                pack.getUpdate_time()
        );
    }
}
