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

import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.util.BitmapLoader;

/**
 * Created by Guohao on 2015/8/2.
 * InvitationsActivity ListView Adapter
 */
public class InvitationsAdapter extends BaseAdapter {
    private List<UserBean> mData;
    private LayoutInflater mInflater;
    private BitmapLoader loader;

    public InvitationsAdapter(Context context, List<UserBean> data) {
        this.mData = data;
        mInflater = LayoutInflater.from(context);
        loader = BitmapLoader.getInstance(context);
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
        ViewHolder viewHolder;
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
        return null;
    }

    class ViewHolder {
        public ImageView icon;
        public TextView nickname;
        public TextView id;
        public ImageView gender;
        public Button button;
    }
}
