package cn.thu.guohao.simplechat.adapter;

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

import cn.thu.guohao.simplechat.R;

/**
 * Created by Guohao on 2015/7/27.
 * Contact list adapter
 */
public class ContactAdapter extends BaseAdapter
        implements AbsListView.OnScrollListener {

    private LayoutInflater mInflater;
    private List<ContactBean> mData;

    public ContactAdapter(Context context, List<ContactBean> data, ListView listView) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        listView.setOnScrollListener(this);
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
            convertView = mInflater.inflate(R.layout.item_contact, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.id_iv_contact_item);
            viewHolder.nickname = (TextView) convertView.findViewById(R.id.id_tv_contact_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.icon.setImageResource(R.mipmap.ic_launcher);
        viewHolder.nickname.setText(mData.get(position).nickname);
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    class ViewHolder {
        public ImageView icon;
        public TextView nickname;
    }
}