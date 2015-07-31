package cn.thu.guohao.simplechat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.thu.guohao.simplechat.R;

/**
 * Created by Guohao on 2015/7/23.
 * Adapter for chats list view;
 */
public class ChatsAdapter extends BaseAdapter
        implements AbsListView.OnScrollListener {

    private Context mContext;
    private List<ChatBean> mListBean;
    private LayoutInflater mInflater;
    private ListView mListView;

    private LruCache<String, Bitmap> mCache;
    private String mImgDir;
    private BitmapFactory.Options mOptions;

    public ChatsAdapter(Context context, List<ChatBean> beans, ListView listView) {
        mContext = context;
        mListBean = beans;
        mInflater = LayoutInflater.from(context);
        mListView = listView;
        mListView.setOnScrollListener(this);

        mImgDir = context.getFilesDir() + "/";
        mOptions = new BitmapFactory.Options();
        mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        mCache = new LruCache<String, Bitmap>(maxMemory / 4) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    private Bitmap getBitmapFromCache(int position) {
        String username = mListBean.get(position).username;
        Bitmap bitmap = mCache.get(username);
        if (bitmap != null) return bitmap;
        String filepath = mImgDir + username;
        if (new File(filepath).exists()) {
            bitmap = BitmapFactory.decodeFile(filepath, mOptions);
            mCache.put(username, bitmap);
            return bitmap;
        }
        String uri = mListBean.get(position).uri;
        if (uri.equals("null")) return null;
        new DownloadTask(username).execute(uri);
        return null;
    }

    class DownloadTask extends AsyncTask<String, Void, Bitmap> {
        private String username;
        public DownloadTask(String username) {
            this.username = username;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromCloud(params[0]);
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i("lgh", "Download Image from Server: " + username + ".png");
            super.onPostExecute(bitmap);
            ImageView view = (ImageView) mListView.findViewWithTag(username);
            view.setImageBitmap(bitmap);
            saveUserBitmap(username, bitmap);
        }
    }

    private void saveUserBitmap(String username, Bitmap bitmap) {
        String saveFilename = username + ".png";
        FileOutputStream outputStream;
        try {
            outputStream = mContext.openFileOutput(saveFilename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromCloud(String uri) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
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
        viewHolder.unread.setText("" + mListBean.get(position).unread);
        viewHolder.icon.setTag(mListBean.get(position).username);
        Bitmap bitmap = getBitmapFromCache(position);
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
