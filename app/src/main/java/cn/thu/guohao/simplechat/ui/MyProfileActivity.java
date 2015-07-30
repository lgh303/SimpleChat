package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.User;

public class MyProfileActivity extends ActionBarActivity {

    private RelativeLayout mPhotoLayout, mNameLayout, mIDLayout, mGenderLayout;
    private TextView mNameTextView, mIDTextView, mGenderTextView;

    private User mCurrUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mCurrUser = User.getCurrentUser(this, User.class);
        initView();
        initEvent();
    }

    private void initView() {
        mPhotoLayout = (RelativeLayout) findViewById(R.id.id_li_profile_photo);
        mNameLayout = (RelativeLayout) findViewById(R.id.id_li_profile_name);
        mIDLayout = (RelativeLayout) findViewById(R.id.id_li_profile_id);
        mGenderLayout = (RelativeLayout) findViewById(R.id.id_li_profile_gender);
        mNameTextView = (TextView) findViewById(R.id.id_tv_my_name);
        mIDTextView = (TextView) findViewById(R.id.id_tv_my_id);
        mGenderTextView = (TextView) findViewById(R.id.id_tv_my_gender);
        mNameTextView.setText(mCurrUser.getNickname());
        mIDTextView.setText(mCurrUser.getUsername());
        String gender = getString(R.string.hint_female);
        if (mCurrUser.getIsMale())
            gender = getString(R.string.hint_male);
        mGenderTextView.setText(gender);
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        mGenderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
