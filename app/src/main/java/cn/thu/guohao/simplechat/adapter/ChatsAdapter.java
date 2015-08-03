package cn.thu.guohao.simplechat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.util.BitmapLoader;

/**
 * Created by Guohao on 2015/7/23.
 * Adapter for chats list view;
 */
public class ChatsAdapter extends BaseAdapter
        implements AbsListView.OnScrollListener {
    private List<ChatBean> mListBean;
    private LayoutInflater mInflater;
    private ListView mListView;

    private BitmapLoader loader;

    public ChatsAdapter(Context context, List<ChatBean> beans, ListView listView) {
        mListBean = beans;
        mInflater = LayoutInflater.from(context);
        mListView = listView;
        listView.setOnScrollListener(this);
        loader = BitmapLoader.getInstance(context);
    }

    @Override
    public int getCount() {
        return mListBean.size();
    }

    @Override
    public Object getItem(int position) {
        return mListBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_chats, null);
            viewHolder.unread = (TextView) convertView.findViewById(R.id.id_tv_chats_unread);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.id_iv_chats);
            viewHolder.title = (TextView) convertView.findViewById(R.id.id_tv_title);
            viewHolder.content = (TextView) convertView.findViewById(R.id.id_tv_content);
            viewHolder.time = (TextView) convertView.findViewById(R.id.id_tv_chats_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int unread = mListBean.get(position).unread;
        if (unread > 0) {
            viewHolder.unread.setText("" + unread);
            viewHolder.unread.setVisibility(View.VISIBLE);
        } else {
            viewHolder.unread.setVisibility(View.INVISIBLE);
        }
        viewHolder.icon.setTag(mListBean.get(position).username);
        Bitmap bitmap = loader.getBitmapFromCache(
                mListBean.get(position).username,
                mListBean.get(position).uri,
                mListView);
        if (bitmap == null)
            viewHolder.icon.setImageResource(R.mipmap.ic_launcher);
        else
            viewHolder.icon.setImageBitmap(bitmap);
        viewHolder.title.setText(mListBean.get(position).title);
        viewHolder.content.setText(mListBean.get(position).content);
        viewHolder.time.setText(mListBean.get(position).time);
        return convertView;
    }

    class ViewHolder {
        public TextView unread;
        public ImageView icon;
        public TextView title;
        public TextView content;
        public TextView time;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
