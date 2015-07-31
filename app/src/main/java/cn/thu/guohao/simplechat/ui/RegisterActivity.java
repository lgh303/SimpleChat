package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.Conversation;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.ChatsDAO;
import cn.thu.guohao.simplechat.db.ConversationBean;
import cn.thu.guohao.simplechat.db.MessageBean;
import cn.thu.guohao.simplechat.db.MessageDAO;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.db.UserDAO;


public class RegisterActivity extends ActionBarActivity {

    private EditText mIDEditText;
    private EditText mNameEditText;
    private EditText mPasswordEditText, mConfirmEditText;
    private RadioGroup mSexRadioGroup;
    private Button mRegisterButton;
    private ProgressBar mProgressBar;

    private String username, nickname, password, confirm;
    private boolean isMale;
    private User user;
    private User filehelper;
    private Conversation mConv1, mConv2;

    private UserDAO mUserDAO;
    private ChatsDAO mChatsDAO;
    private MessageDAO mMessageDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initEvent();
    }

    private void initView() {
        mIDEditText = (EditText) findViewById(R.id.id_et_reg_id);
        mNameEditText = (EditText) findViewById(R.id.id_et_reg_username);
        mPasswordEditText = (EditText) findViewById(R.id.id_et_reg_password);
        mConfirmEditText = (EditText) findViewById(R.id.id_et_reg_confirm);
        mRegisterButton = (Button) findViewById(R.id.id_bt_register);
        mSexRadioGroup = (RadioGroup) findViewById(R.id.id_rg_register_sex);
        mProgressBar = (ProgressBar) findViewById(R.id.id_pb_register);
    }

    private void initEvent() {
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mIDEditText.getText().toString();
                nickname = mNameEditText.getText().toString();
                password = mPasswordEditText.getText().toString();
                confirm = mConfirmEditText.getText().toString();
                isMale = (mSexRadioGroup.getCheckedRadioButtonId() == R.id.id_rb_register_male);

                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                mRegisterButton.setClickable(false);
                if (checkInput())
                    checkRegister();
                else
                    restoreUI();
            }
        });
    }

    private void restoreUI() {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mRegisterButton.setClickable(true);
    }

    private boolean checkInput() {
        if (nickname.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Nickname Invalid", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Password Invalid", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(RegisterActivity.this, "Two Passwords Not Equal", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkRegister() {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list.isEmpty())
                    register();
                else {
                    Toast.makeText(RegisterActivity.this, "Chat ID Exists", Toast.LENGTH_SHORT).show();
                    restoreUI();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(RegisterActivity.this, "Unexpected Register Error", Toast.LENGTH_SHORT).show();
                restoreUI();
            }
        });
    }

    private void register() {
        Log.i("lgh", "register: username=" + username +
                        ",nick=" + nickname +
                        ",password=" + password +
                        ",isMale=" + isMale +
                        ",photoUri=" + "null"
        );

        user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setIsMale(isMale);
        user.setPhotoUri("null");
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                mUserDAO = new UserDAO(RegisterActivity.this, user.getUsername());
                int sex = 0, type = 0;
                if (user.getIsMale()) sex = 1;
                mUserDAO.insert(new UserBean(
                        user.getUsername(), user.getNickname(),
                        sex, type, "null"));
                mChatsDAO = new ChatsDAO(RegisterActivity.this, user.getUsername());
                mMessageDAO = new MessageDAO(RegisterActivity.this, user.getUsername());
                initSpecialUsers();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(RegisterActivity.this, "Unexpected Register Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSpecialUsers() {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", "filehelper");
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (!list.isEmpty()) {
                    filehelper = list.get(0);
                    int sex = 0, type = 0;
                    if (filehelper.getIsMale()) sex = 1;
                    mUserDAO.insert(new UserBean(
                            filehelper.getUsername(), filehelper.getNickname(),
                            sex, type, filehelper.getPhotoUri()));
                    initSpecialConversations();
                }
            }

            @Override
            public void onError(int i, String s) { }
        });
    }

    private void initSpecialConversations() {
        mConv1 = new Conversation();
        setConversation(mConv1, user, user);
        mConv1.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                updateLocal(mConv1);
                mConv2 = new Conversation();
                setConversation(mConv2, user, filehelper);
                mConv2.save(RegisterActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        updateLocal(mConv2);
                        addSpecialUsers();
                    }
                    @Override
                    public void onFailure(int i, String s) { }
                });
            }
            @Override
            public void onFailure(int i, String s) { }
        });
    }

    private void setConversation(Conversation mConv, User aUser, User bUser) {
        mConv.setaUser(aUser);
        mConv.setaUsername(aUser.getUsername());
        mConv.setaNickname(aUser.getNickname());
        mConv.setbUser(bUser);
        mConv.setbUsername(bUser.getUsername());
        mConv.setbNickname(bUser.getNickname());
        mConv.setUnread(1);
        mConv.setLatestMessage(getString(R.string.chat_first_message));
    }

    private void updateLocal(Conversation mConv) {
        mChatsDAO.insertConversation(new ConversationBean(
                mConv.getObjectId(),
                mConv.getbNickname(),
                mConv.getbUsername(),
                0,
                mConv.getLatestMessage(),
                mConv.getCreatedAt()));
        mMessageDAO.createMessageConvTable(mConv.getbUsername());
        mMessageDAO.insertMessageToConvTable(
                mConv.getbUsername(),
                new MessageBean(
                        2, 0, "Notification",
                        getString(R.string.chat_first_message),
                        null, mConv.getUpdatedAt()
                ),
                true
        );
        mChatsDAO.updateConversation(
                mConv.getbUsername(),
                mConv.getbNickname(),
                getString(R.string.chat_first_message),
                mConv.getCreatedAt(),
                1
        );
    }

    private void addSpecialUsers() {
        BmobRelation friends = new BmobRelation();
        friends.add(user);
        friends.add(filehelper);
        user.setFriends(friends);
        BmobRelation conversations = new BmobRelation();
        conversations.add(mConv1);
        conversations.add(mConv2);
        user.setConversations(conversations);
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                login();
            }
            @Override
            public void onFailure(int i, String s) {
                Log.i("lgh", "Update Fail " + i + " " + s);
            }
        });
    }

    private void login() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
