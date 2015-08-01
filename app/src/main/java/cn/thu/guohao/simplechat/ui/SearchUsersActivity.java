package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.adapter.ContactAdapter;
import cn.thu.guohao.simplechat.adapter.ContactBean;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.fragment.ContactsFragment;

public class SearchUsersActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private EditText mEditText;
    private Button mSearchButton;
    private ListView mListView;
    private List<ContactBean> mData = new ArrayList<>();
    private String mText = "", mLastText = "@#$%^&*$%^&*$$#$%^&";
    private ContactAdapter mAdapter;
    private List<UserBean> mUserData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        mEditText = (EditText) findViewById(R.id.id_et_search_users);
        mSearchButton = (Button) findViewById(R.id.id_bt_search_users);
        mSearchButton.setClickable(false);
        mListView = (ListView) findViewById(R.id.id_lv_search_users);
        initEvent();
    }

    private void initEvent() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                mText = mEditText.getText().toString();
            }
        });
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mText.equals(mLastText)) return;
                mLastText = mText;
                mData.clear();
                mUserData.clear();
                BmobQuery<User> query1 = new BmobQuery<>();
                query1.addWhereContains("username", mText);
                BmobQuery<User> query2 = new BmobQuery<>();
                query2.addWhereContains("nickname", mText);
                List<BmobQuery<User>> queries = new ArrayList<>();
                queries.add(query1);
                queries.add(query2);
                BmobQuery<User> mainQuery = new BmobQuery<>();
                mainQuery.or(queries);
                mainQuery.findObjects(SearchUsersActivity.this, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        for (User user : list) {
                            mData.add(new ContactBean(
                                    user.getUsername(), user.getNickname(),
                                    user.getPhotoUri()
                            ));
                            int sex = 0;
                            if (user.getIsMale()) sex = 1;
                            int type = 0;
                            mUserData.add(new UserBean(
                                    user.getUsername(), user.getNickname(),
                                    sex, type, user.getPhotoUri()));
                        }
                        mAdapter = new ContactAdapter(SearchUsersActivity.this, mData, mListView);
                        mListView.setAdapter(mAdapter);
                        mListView.setOnItemClickListener(SearchUsersActivity.this);
                    }
                    @Override
                    public void onError(int i, String s) {}
                });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_users, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserBean bean = mUserData.get(position);
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("search_user", bean);
        startActivity(intent);
    }
}
