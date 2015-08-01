package cn.thu.guohao.simplechat.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.thu.guohao.simplechat.data.Delivery;
import cn.thu.guohao.simplechat.data.Installation;
import cn.thu.guohao.simplechat.data.User;

/**
 * Created by Guohao on 2015/8/1.
 * Send Json String to Server
 */
public class DeliverySender {

    private Context context;
    private String mUsername, mJsonString;
    private BmobPushManager<Installation> mPushManager;

    public DeliverySender(Context context) {
        this.context = context;
        mPushManager = new BmobPushManager<>(context);
    }

    public void send(final String username, String jsonString) {
        mUsername = username;
        mJsonString = jsonString;
        BmobQuery<Installation> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);
        query.findObjects(context, new FindListener<Installation>() {
            @Override
            public void onSuccess(List<Installation> list) {
                if (!list.isEmpty())
                    doSend(true);
                else
                    doSend(false);
            }
            @Override
            public void onError(int i, String s) {}
        });
    }

    private void doSend(boolean hasInstallation) {
        if (hasInstallation) {
            BmobQuery<Installation> query = Installation.getQuery();
            query.addWhereEqualTo("username", mUsername);
            mPushManager.setQuery(query);
            try {
                JSONObject json = new JSONObject(mJsonString);
                mPushManager.pushMessage(json);
                Toast.makeText(context, "Message Sent..", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, "Send Message Failed..", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("username", mUsername);
            query.findObjects(context, new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {
                    if (!list.isEmpty()) {
                        saveUpdateDelivery();
                    }
                }
                @Override
                public void onError(int i, String s) {}
            });
        }
    }

    private void saveUpdateDelivery() {
        final Delivery delivery = new Delivery();
        delivery.setReceiver(mUsername);
        delivery.setJson(mJsonString);
        delivery.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i("lgh", "Saved in wait list");
                Toast.makeText(context, "Message Sent..", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int i, String s) {
                Log.i("lgh", "Save Failed " + s);
            }
        });
    }
}
