package cn.thu.guohao.simplechat.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.db.UserDAO;

public class ProfileActivity extends ActionBarActivity {

    private TextView mNicknameTextView;
    private TextView mUsernameTextView;
    private ImageView mSexImageView;
    private Button mMessageButton;

    private UserDAO mUserDAO;
    private UserBean mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String currUser = getIntent().getStringExtra("currUser");
        String username = getIntent().getStringExtra("username");
        mUserDAO = new UserDAO(this, currUser);
        mUser = mUserDAO.get(username);
        initView();
        initEvent();
    }

    private void initView() {
        mNicknameTextView = (TextView) findViewById(R.id.id_tv_profile_nickname);
        mUsernameTextView = (TextView) findViewById(R.id.id_tv_profile_username);
        mSexImageView = (ImageView) findViewById(R.id.id_iv_profile_sex);
        mMessageButton = (Button) findViewById(R.id.id_bt_profile_message);
        mNicknameTextView.setText(mUser.getNickname());
        mUsernameTextView.setText(getString(R.string.prefix_id) + mUser.getUsername());
        if (mUser.getSex() == 0)
            mSexImageView.setImageResource(R.drawable.profile_woman);
        else
            mSexImageView.setImageResource(R.drawable.profile_man);
    }

    private void initEvent() {
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
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
