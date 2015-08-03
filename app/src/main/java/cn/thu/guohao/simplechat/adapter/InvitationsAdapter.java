package cn.thu.guohao.simplechat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.Conversation;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.ChatsDAO;
import cn.thu.guohao.simplechat.db.MessageDAO;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.db.UserDAO;
import cn.thu.guohao.simplechat.util.BitmapLoader;
import cn.thu.guohao.simplechat.util.ConversationBuilder;
import cn.thu.guohao.simplechat.util.DeliverySender;
import cn.thu.guohao.simplechat.util.InfoPack;
import cn.thu.guohao.simplechat.util.Utils;

/**
 * Created by Guohao on 2015/8/2.
 * InvitationsActivity ListView Adapter
 */
public class InvitationsAdapter extends BaseAdapter {
    private Context mContext;
    private List<UserBean> mData;
    private LayoutInflater mInflater;
    private BitmapLoader loader;
    private UserDAO mUserDAO;
    private ConversationBuilder mConvBuilder;
    private User mCurrUser;

    public InvitationsAdapter(Context context, List<UserBean> data, User user) {
        this.mContext = context;
        this.mData = data;
        this.mCurrUser = user;
        mInflater = LayoutInflater.from(context);
        loader = BitmapLoader.getInstance(context);
        mUserDAO = new UserDAO(context, user.getUsername());
        mConvBuilder = new ConversationBuilder(
                context,
                new ChatsDAO(context, user.getUsername()),
                new MessageDAO(context, user.getUsername())
        );
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final UserBean user = mData.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_invitations, null);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.id_iv_profile_photo);
            viewHolder.nickname = (TextView) convertView.findViewById(R.id.id_tv_profile_nickname);
            viewHolder.id = (TextView) convertView.findViewById(R.id.id_tv_profile_username);
            viewHolder.gender = (ImageView) convertView.findViewById(R.id.id_iv_profile_sex);
            viewHolder.button = (Button) convertView.findViewById(R.id.id_bt_profile_accept);
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("lgh", "onAcceptButton: " + user.toString());
                    Button bt = (Button)v;
                    bt.setText(mContext.getString(R.string.added));
                    bt.setClickable(false);
                    bt.setBackgroundResource(R.drawable.selector_gray_background);
                    user.setType(UserDAO.FRIENDS);
                    mUserDAO.update(user);
                    initConversation(user.getUsername());
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nickname.setText(user.getNickname());
        viewHolder.id.setText(user.getUsername());
        Bitmap bitmap = loader.getBitmapFromCache(
                user.getUsername(),
                user.getPhotoUri(),
                viewHolder.icon);
        if (bitmap == null)
            viewHolder.icon.setImageResource(R.mipmap.ic_launcher);
        else
            viewHolder.icon.setImageBitmap(bitmap);
        if (user.getSex() == 1)
            viewHolder.gender.setImageResource(R.drawable.profile_man);
        else
            viewHolder.gender.setImageResource(R.drawable.profile_woman);
        if (user.getType() == UserDAO.MY_ADMIRER) {
            viewHolder.button.setText(mContext.getString(R.string.accept));
            viewHolder.button.setBackgroundResource(R.drawable.selector_button_blue);
        }
        else {
            viewHolder.button.setText(mContext.getString(R.string.pending));
            viewHolder.button.setClickable(false);
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView icon;
        public TextView nickname;
        public TextView id;
        public ImageView gender;
        public Button button;
    }

    private void initConversation(final String friendUsername) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", friendUsername);
        query.findObjects(mContext, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (!list.isEmpty()) {
                    final User friend = list.get(0);
                    final Conversation conv = new Conversation();
                    mConvBuilder.setConversation(conv, mCurrUser, friend);
                    conv.save(mContext, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            mConvBuilder.updateLocal(conv);
                            sendAcceptReplay(
                                    friendUsername,
                                    conv.getObjectId(),
                                    conv.getCreatedAt()
                            );
                            mConvBuilder.updateFriendsCloud(mCurrUser, friend);
                        }
                        @Override
                        public void onFailure(int i, String s) {}
                    });
                }
            }
            @Override
            public void onError(int i, String s) {}
        });
    }

    private void sendAcceptReplay(String friendUsername, String convID, String time) {
        String jsonString = Utils.makeJsonString(
                InfoPack.STR_ACCEPT,
                mCurrUser.getUsername(),
                convID, null, time
        );
        DeliverySender.getInstance(mContext).send(
                InfoPack.TYPE.ACCEPT,
                friendUsername,
                jsonString
        );
    }
}
