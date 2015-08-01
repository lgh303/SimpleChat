package cn.thu.guohao.simplechat.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.Installation;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.db.UserDAO;
import cn.thu.guohao.simplechat.util.BitmapLoader;
import cn.thu.guohao.simplechat.util.InfoPack;
import cn.thu.guohao.simplechat.util.Utils;

public class MyProfileActivity extends ActionBarActivity {

    private RelativeLayout mPhotoLayout, mNameLayout, mGenderLayout;
    private TextView mNameTextView, mIDTextView, mGenderTextView;
    private ImageView mPhotoImageView;

    private User mCurrUser;
    private UserDAO mUserDAO;
    private BmobPushManager<Installation> mPushManager;
    private String[] genderArr;

    private BitmapLoader loader;

    private static final int REQUEST_NICKNAME = 0;
    private static final int REQUEST_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mCurrUser = User.getCurrentUser(this, User.class);
        mUserDAO = new UserDAO(this, mCurrUser.getUsername());
        mPushManager = new BmobPushManager<>(this);
        loader = BitmapLoader.getInstance(this);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrUser = User.getCurrentUser(this, User.class);
        mNameTextView.setText(mCurrUser.getNickname());
        mIDTextView.setText(mCurrUser.getUsername());
        mGenderTextView.setText(gender2String(mCurrUser.getIsMale()));
        Bitmap bitmap = loader.getBitmapFromCache(
                mCurrUser.getUsername(),
                mCurrUser.getPhotoUri(),
                mPhotoImageView
        );
        if (bitmap != null)
            mPhotoImageView.setImageBitmap(bitmap);
    }

    private void initView() {
        mPhotoLayout = (RelativeLayout) findViewById(R.id.id_li_profile_photo);
        mNameLayout = (RelativeLayout) findViewById(R.id.id_li_profile_name);
        mGenderLayout = (RelativeLayout) findViewById(R.id.id_li_profile_gender);
        mNameTextView = (TextView) findViewById(R.id.id_tv_my_name);
        mIDTextView = (TextView) findViewById(R.id.id_tv_my_id);
        mGenderTextView = (TextView) findViewById(R.id.id_tv_my_gender);
        mPhotoImageView = (ImageView) findViewById(R.id.id_iv_my_photo);
        genderArr = new String[] {
                getString(R.string.hint_male),
                getString(R.string.hint_female)
        };
    }

    private void initEvent() {
        mPhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoIntent, REQUEST_PHOTO);
            }
        });
        mNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, EditActivity.class);
                startActivityForResult(intent, REQUEST_NICKNAME);
            }
        });
        mGenderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setTitle(getString(R.string.choose_gender));
                int choice = 1;
                if (mCurrUser.getIsMale()) choice = 0;
                builder.setSingleChoiceItems(genderArr, choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String gender = genderArr[which];
                        updateUserRemote(
                                mCurrUser.getNickname(), gender,
                                mCurrUser.getPhotoUri()
                        );
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void updateUserRemote(String nickname, String gender, String uri) {
        User user = new User();
        user.setNickname(nickname);
        user.setIsMale(gender.equals(getString(R.string.hint_male)));
        user.setPhotoUri(uri);
        user.update(this, mCurrUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                mCurrUser = User.getCurrentUser(MyProfileActivity.this, User.class);
                mNameTextView.setText(mCurrUser.getNickname());
                mGenderTextView.setText(gender2String(mCurrUser.getIsMale()));
                Toast.makeText(MyProfileActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
                sendUpdateMessage();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(MyProfileActivity.this, "Update Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendUpdateMessage() {
        ArrayList<UserBean> friends = mUserDAO.get();
        for (UserBean friend : friends) {
            if (friend.getUsername().equals("filehelper"))
                continue;
            // TODO check if installation can be found.
            doSendMessage(friend.getUsername());
        }
    }

    private void doSendMessage(String username) {
        BmobQuery<Installation> query = Installation.getQuery();
        query.addWhereEqualTo("username", username);
            mPushManager.setQuery(query);
            try {
                JSONObject json = new JSONObject(Utils.makeJsonString(
                        InfoPack.STR_USER_UPDATE,
                        mCurrUser.getUsername(),
                        mCurrUser.getNickname(),
                        mCurrUser.getPhotoUri(),
                        gender2String(mCurrUser.getIsMale())
                ));
                mPushManager.pushMessage(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private String gender2String(boolean isMale) {
        String gender = getString(R.string.hint_female);
        if (isMale)
            gender = getString(R.string.hint_male);
        return gender;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NICKNAME && resultCode == RESULT_OK) {
            updateUserRemote(
                    data.getStringExtra("nickname"),
                    data.getStringExtra("gender"),
                    mCurrUser.getPhotoUri()
            );
        } else if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {
            updatePhoto(data.getData());
        }
    }

    private void updatePhoto(Uri data) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(
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
        Bitmap bitmap = Bitmap.createScaledBitmap(b, 128, 128, true);
        String saveFilename = mCurrUser.getUsername() + ".png";
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(saveFilename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            b.recycle();
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loader.removeBitmapFromCache(mCurrUser.getUsername());
        return getFilesDir() + "/" + saveFilename;
    }

    private void uploadPhotoRemote(String filePath) {
        BmobProFile.getInstance(MyProfileActivity.this).upload(filePath, new UploadListener() {
            @Override
            public void onSuccess(String filename, String url, BmobFile bmobFile) {
                String URL =BmobProFile.getInstance(MyProfileActivity.this).signURL(
                        filename,
                        url,
                        "a4e4a49c9a03cebfed9b617935b2c29c",
                        0,null);
                Log.i("lgh", mCurrUser.getUsername() + "URL: " + URL);
                updateUserRemote(
                        mCurrUser.getNickname(),
                        gender2String(mCurrUser.getIsMale()),
                        URL
                );
            }
            @Override
            public void onProgress(int i) {}
            @Override
            public void onError(int i, String s) {
                Toast.makeText(MyProfileActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
