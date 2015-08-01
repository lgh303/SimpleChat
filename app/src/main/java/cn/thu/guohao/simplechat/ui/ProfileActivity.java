package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.db.ChatsDAO;
import cn.thu.guohao.simplechat.db.ConversationBean;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.db.UserDAO;
import cn.thu.guohao.simplechat.util.BitmapLoader;

public class ProfileActivity extends ActionBarActivity {

    private TextView mNicknameTextView;
    private TextView mUsernameTextView;
    private ImageView mPhotoImageView;
    private ImageView mSexImageView;
    private Button mMessageButton;

    private UserDAO mUserDAO;
    private ChatsDAO mChatsDAO;
    private UserBean mUser;

    private BitmapLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String currUser = getIntent().getStringExtra("currUser");
        String username = getIntent().getStringExtra("username");
        mUserDAO = new UserDAO(this, currUser);
        mUser = mUserDAO.get(username);
        mChatsDAO = new ChatsDAO(this, currUser);
        loader = BitmapLoader.getInstance(this);
        initView();
        initEvent();
    }

    private void initView() {
        mNicknameTextView = (TextView) findViewById(R.id.id_tv_profile_nickname);
        mUsernameTextView = (TextView) findViewById(R.id.id_tv_profile_username);
        mPhotoImageView = (ImageView) findViewById(R.id.id_iv_profile_photo);
        mSexImageView = (ImageView) findViewById(R.id.id_iv_profile_sex);
        mMessageButton = (Button) findViewById(R.id.id_bt_profile_message);
        mNicknameTextView.setText(mUser.getNickname());
        mUsernameTextView.setText(getString(R.string.prefix_id) + " " + mUser.getUsername());
        if (mUser.getSex() == 0)
            mSexImageView.setImageResource(R.drawable.profile_woman);
        else
            mSexImageView.setImageResource(R.drawable.profile_man);
        Bitmap bitmap = loader.getBitmapFromCache(
                mUser.getUsername(), mUser.getPhotoUri(),
                mPhotoImageView);
        if (bitmap != null)
            mPhotoImageView.setImageBitmap(bitmap);
    }

    private void initEvent() {
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationBean conv = mChatsDAO.getConversation(mUser.getUsername());
                Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
                intent.putExtra("friend_username", mUser.getUsername());
                intent.putExtra("title", mUser.getNickname());
                intent.putExtra("conversationID", conv.getId());
                intent.putExtra("uri", mUser.getPhotoUri());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
