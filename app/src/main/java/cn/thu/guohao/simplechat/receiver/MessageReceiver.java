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
import cn.thu.guohao.simplechat.util.BitmapLoader;
import cn.thu.guohao.simplechat.util.InfoPack;
import cn.thu.guohao.simplechat.util.PackProcessor;
import cn.thu.guohao.simplechat.util.Utils;

/**
 * Created by Guohao on 2015/7/30.
 * Receive message from cloud.
 */
public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String jsonString = intent.getStringExtra(
                    PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            InfoPack pack = Utils.parseMessage(jsonString);
            PackProcessor.getInstance(context).processPack(pack);
        }
    }
}
