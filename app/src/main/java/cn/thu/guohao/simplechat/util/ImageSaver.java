package cn.thu.guohao.simplechat.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;

import java.io.FileOutputStream;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Guohao on 2015/8/3.
 * get image via uri & scale it & save it.
 */
public class ImageSaver {
    private int width;
    private int height;
    private Context context;
    private String filename;
    private OnUploadedListener listener;

    public interface OnUploadedListener {
        void onImageUploaded(String URL);
    }

    public ImageSaver(Context context, String filename, OnUploadedListener listener) {
        width = 128;
        height = 128;
        this.context = context;
        this.filename = filename;
        this.listener = listener;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void saveAndUpload(Uri data) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(
                data, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        Log.i("lgh", "photo filepath: " + filePath);
        String scaled = getScaledPhoto(filePath);
        Log.i("lgh", "Scaled Filepath: " + scaled);
        uploadPhotoRemote(scaled);
    }

    private String getScaledPhoto(String filePath) {
        Bitmap b = BitmapFactory.decodeFile(filePath);
        Bitmap bitmap = Bitmap.createScaledBitmap(b, width, height, true);
        String saveFilename = filename + ".png";
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(saveFilename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            b.recycle();
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.getFilesDir() + "/" + saveFilename;
    }

    private void uploadPhotoRemote(String filePath) {
        BmobProFile.getInstance(context).upload(filePath, new UploadListener() {
            @Override
            public void onSuccess(String filename, String url, BmobFile bmobFile) {
                String URL =BmobProFile.getInstance(context).signURL(
                        filename,
                        url,
                        "a4e4a49c9a03cebfed9b617935b2c29c",
                        0,null);
                Log.i("lgh", filename + "URL: " + URL);
                listener.onImageUploaded(URL);
            }
            @Override
            public void onProgress(int i) {}
            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
