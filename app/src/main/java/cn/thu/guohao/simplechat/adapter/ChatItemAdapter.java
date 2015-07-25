package cn.thu.guohao.simplechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.ui.ChatActivity;

/**
 * Created by Guohao on 2015/7/24.
 * Adapter for Chat Pane
 */
public class ChatItemAdapter extends BaseAdapter
        implements AbsListView.OnScrollListener {

    private LayoutInflater mInflater;
    private List<ChatItemBean> mData;
    private ChatActivity mContext;

    public ChatItemAdapter(ChatActivity context, List<ChatItemBean> data, ListView listview) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = data;
        listview.setOnScrollListener(this);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatItemBean.TYPE type = mData.get(position).type;
        if (type == ChatItemBean.TYPE.LEFT)
            return 0;
        else if (type == ChatItemBean.TYPE.RIGHT)
            return 1;
        return super.getItemViewType(position);
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
        int type = getItemViewType(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            switch (type) {
                case 0:
                    convertView = mInflater.inflate(R.layout.item_left_chat, null);
                    viewHolder.icon = (ImageView) convertView.findViewById(R.id.id_iv_left_chat_item);
                    viewHolder.text = (TextView) convertView.findViewById(R.id.id_tv_left_chat_item);
                    convertView.setTag(viewHolder);
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.item_right_chat, null);
                    viewHolder.icon = (ImageView) convertView.findViewById(R.id.id_iv_right_chat_item);
                    viewHolder.text = (TextView) convertView.findViewById(R.id.id_tv_right_chat_item);
                    convertView.setTag(viewHolder);
                    break;
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.icon.setImageResource(R.mipmap.ic_launcher);
        viewHolder.text.setText(mData.get(position).text);
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            hideKeyboard();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mContext.findViewById(android.R.id.content).getWindowToken(), 0);
    }

    class ViewHolder {
        TextView text;
        ImageView icon;
    }
}
