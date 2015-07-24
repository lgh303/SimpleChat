package cn.thu.guohao.simplechat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Guohao on 2015/7/24.
 * Adapter for Chat Pane
 */
public class ChatItemAdapter extends BaseAdapter
        implements AbsListView.OnScrollListener {

    private LayoutInflater mInflater;
    private List<ChatItemBean> mData;

    public ChatItemAdapter(Context context, List<ChatItemBean> data, ListView listview) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        listview.setOnScrollListener(this);
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_right_chat, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.id_iv_right_chat_item);
            viewHolder.text = (TextView) convertView.findViewById(R.id.id_tv_right_chat_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.icon.setImageResource(R.mipmap.ic_launcher);
        viewHolder.text.setText(mData.get(position).text);
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    class ViewHolder {
        TextView text;
        ImageView icon;
    }
}
