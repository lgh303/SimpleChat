package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.adapter.InvitationsAdapter;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.db.UserDAO;

public class InvitationsActivity extends ActionBarActivity {

    private Button mInviteButton;
    private ListView mListView;
    private List<UserBean> mData;
    private User mCurrUser;
    private UserDAO mUserDAO;
    private InvitationsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitations);
        mInviteButton = (Button) findViewById(R.id.id_bt_invitations_invite);
        mListView = (ListView) findViewById(R.id.id_lv_invitations);
        mCurrUser = User.getCurrentUser(this, User.class);
        mUserDAO = new UserDAO(this, mCurrUser.getUsername());
        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvitationsActivity.this, SearchUsersActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mData = mUserDAO.getPending();
        mAdapter = new InvitationsAdapter(this, mData, mListView, mCurrUser);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invitations, menu);
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
