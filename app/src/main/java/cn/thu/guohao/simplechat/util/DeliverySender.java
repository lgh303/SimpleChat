package cn.thu.guohao.simplechat.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.thu.guohao.simplechat.data.Delivery;
import cn.thu.guohao.simplechat.data.Installation;
import cn.thu.guohao.simplechat.data.User;

/**
 * Created by Guohao on 2015/8/1.
 * Send Json String to Server
 */
public class DeliverySender {

    private static DeliverySender sender;
    public static DeliverySender getInstance(Context context) {
        if (sender == null) {
            sender = new DeliverySender(context);
            return sender;
        }
        sender.context = context;
        //sender.mPushManager = new BmobPushManager<>(context);
        return sender;
    }

    private Context context;
    private BmobPushManager<Installation> mPushManager;

    private DeliverySender(Context context) {
        this.context = context;
        mPushManager = new BmobPushManager<>(context);
    }

    public void send(final InfoPack.TYPE type, final String username, final String jsonString) {
        BmobQuery<Installation> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);
        query.findObjects(context, new FindListener<Installation>() {
            @Override
            public void onSuccess(List<Installation> list) {
                if (!list.isEmpty())
                    doSend(true, type, username, jsonString);
                else
                    doSend(false, type, username, jsonString);
            }
            @Override
            public void onError(int i, String s) {}
        });
    }

    private void doSend(boolean hasInstallation, final InfoPack.TYPE type, final String username, final String jsonString) {
        if (hasInstallation) {
            BmobQuery<Installation> query = Installation.getQuery();
            query.addWhereEqualTo("username", username);
            mPushManager.setQuery(query);
            try {
                JSONObject json = new JSONObject(jsonString);
                mPushManager.pushMessage(json);
                postPushSuccess(type);
            } catch (JSONException e) {
                postPushFail(type);
                e.printStackTrace();
            }
        } else {
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("username", username);
            query.findObjects(context, new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {
                    if (!list.isEmpty()) {
                        saveUpdateDelivery(type, username, jsonString);
                    } else
                        postPushFail(type);
                }
                @Override
                public void onError(int i, String s) {
                    postPushFail(type);
                }
            });
        }
    }

    private void saveUpdateDelivery(final InfoPack.TYPE type, String username, String jsonString) {
        final Delivery delivery = new Delivery();
        delivery.setReceiver(username);
        delivery.setJson(jsonString);
        delivery.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i("lgh", "Saved in wait list");
                postPushSuccess(type);
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i("lgh", "Save Failed " + s);
                postPushFail(type);
            }
        });
    }

    private void postPushSuccess(InfoPack.TYPE type) {
        if (type == InfoPack.TYPE.INVITE)
            Toast.makeText(context, "Invitation Sent Successfully", Toast.LENGTH_SHORT).show();
    }

    private void postPushFail(InfoPack.TYPE type) {
        if (type == InfoPack.TYPE.INVITE)
            Toast.makeText(context, "Send Invitation Failed..", Toast.LENGTH_SHORT).show();
    }
}
