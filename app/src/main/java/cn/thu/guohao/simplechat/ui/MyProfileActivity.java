package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.UpdateListener;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.Installation;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.db.UserDAO;
import cn.thu.guohao.simplechat.util.InfoPack;
import cn.thu.guohao.simplechat.util.Utils;

public class MyProfileActivity extends ActionBarActivity {

    private RelativeLayout mPhotoLayout, mNameLayout, mGenderLayout;
    private TextView mNameTextView, mIDTextView, mGenderTextView;

    private User mCurrUser;
    private UserDAO mUserDAO;
    private BmobPushManager<Installation> mPushManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mCurrUser = User.getCurrentUser(this, User.class);
        mUserDAO = new UserDAO(this, mCurrUser.getUsername());
        mPushManager = new BmobPushManager<>(this);
        initView();
        initEvent();

    }

    private void initView() {
        mPhotoLayout = (RelativeLayout) findViewById(R.id.id_li_profile_photo);
        mNameLayout = (RelativeLayout) findViewById(R.id.id_li_profile_name);
        mGenderLayout = (RelativeLayout) findViewById(R.id.id_li_profile_gender);
        mNameTextView = (TextView) findViewById(R.id.id_tv_my_name);
        mIDTextView = (TextView) findViewById(R.id.id_tv_my_id);
        mGenderTextView = (TextView) findViewById(R.id.id_tv_my_gender);
        mNameTextView.setText(mCurrUser.getNickname());
        mIDTextView.setText(mCurrUser.getUsername());
        mGenderTextView.setText(gender2String(mCurrUser.getIsMale()));
    }

    private void initEvent() {
        mPhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, EditActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        mGenderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void updateUserRemote(String nickname, String gender) {
        User user = new User();
        user.setNickname(nickname);
        user.setIsMale(gender.equals(getString(R.string.hint_male)));
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
                        gender2String(mCurrUser.getIsMale()),
                        "null"
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
        if (resultCode == 303) {
            updateUserRemote(
                    data.getStringExtra("nickname"),
                    data.getStringExtra("gender")
            );
        }
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
