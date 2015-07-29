package cn.thu.guohao.simplechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.bmob.push.PushConstants;

/**
 * Created by Guohao on 2015/7/29.
 * Pushed Message Receiver
 */
public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            Log.i("lgh", "Message: " + intent.getStringExtra(
                    PushConstants.EXTRA_PUSH_MESSAGE_STRING));
        }
    }

}
