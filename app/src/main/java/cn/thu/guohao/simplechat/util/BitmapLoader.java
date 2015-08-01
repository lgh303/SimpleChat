package cn.thu.guohao.simplechat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Guohao on 2015/8/1.
 * Load profile photo
 */
public class BitmapLoader {
    private static BitmapLoader loader;

    public static BitmapLoader getInstance(Context context) {
        if (loader == null)
            loader = new BitmapLoader(context);
        loader.mContext = context;
        return loader;
    }

    private Context mContext;
    private LruCache<String, Bitmap> mCache;
    private String mImgDir;
    private BitmapFactory.Options mOptions;

    private BitmapLoader(Context context) {
        mContext = context;
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

    public static boolean hasNoCache() {
        return loader == null;
    }

    public void removeBitmapFromCache(String username) {
        mCache.remove(username);
    }
    public Bitmap getBitmapFromCache(String username, String uri, ImageView view) {
        Bitmap bitmap = mCache.get(username);
        if (bitmap != null) return bitmap;
        String filepath = mImgDir + username + ".png";
        if (new File(filepath).exists()) {
            bitmap = BitmapFactory.decodeFile(filepath, mOptions);
            mCache.put(username, bitmap);
            return bitmap;
        }
        if (uri.equals("null")) return null;
        new DownloadTask(username, view).execute(uri);
        return null;
    }

    class DownloadTask extends AsyncTask<String, Void, Bitmap> {
        private String username;
        private ImageView view;
        public DownloadTask(String username, ImageView view) {
            this.username = username;
            this.view = view;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromCloud(params[0]);
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i("lgh", "Download Image from Server: " + username + ".png");
            super.onPostExecute(bitmap);
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

}
