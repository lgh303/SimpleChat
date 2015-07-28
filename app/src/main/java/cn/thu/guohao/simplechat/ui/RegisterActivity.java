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
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        if (checkInput())
                            checkRegister();
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        mRegisterButton.setClickable(true);
                    }
                }.execute();
            }
        });
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
                else
                    Toast.makeText(RegisterActivity.this, "Chat ID Exists", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(RegisterActivity.this, "Unexpected Register Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register() {
        Log.i("lgh", "register: username=" + username +
                        ",nick=" + nickname +
                        ",password=" + password +
                        ",isMale=" + isMale
        );

        user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setIsMale(isMale);
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
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
                    Log.i("lgh", "filehelper: " + filehelper);
                    if (filehelper != null)
                        Log.i("lgh", "filehelper ID: " + filehelper.getObjectId());
                    initSpecialConversations();
                } else
                    Log.i("lgh", "filehelper not found!");
            }

            @Override
            public void onError(int i, String s) {
                Log.i("lgh", "filehelper not found!");
            }
        });
    }

    private void initSpecialConversations() {
        mConv1 = new Conversation();
        mConv1.setaUser(user);
        mConv1.setaUsername(user.getUsername());
        mConv1.setaNickname(user.getNickname());
        mConv1.setbUser(user);
        mConv1.setbUsername(user.getUsername());
        mConv1.setbNickname(user.getNickname());
        mConv1.setLatestMessage(getString(R.string.chat_first_message));
        mConv1.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i("lgh", "mConv1.id=" + mConv1.getObjectId());
                mConv2 = new Conversation();
                mConv2.setaUser(user);
                mConv2.setaUsername(user.getUsername());
                mConv2.setaNickname(user.getNickname());
                mConv2.setbUser(filehelper);
                mConv2.setbUsername(filehelper.getUsername());
                mConv2.setbNickname(filehelper.getNickname());
                mConv2.setLatestMessage(getString(R.string.chat_first_message));
                mConv2.save(RegisterActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Log.i("lgh", "mConv2.id=" + mConv2.getObjectId());
                        addSpecialUsers();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
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
                Log.i("lgh", "Update Success");
                login();
            }
            @Override
            public void onFailure(int i, String s) {
                Log.i("lgh", "Update Fail " + i + " " + s);
            }
        });
    }

    private void login() {
        Intent intent = new Intent(RegisterActivity.this, RegisterSuccessActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("nickname", nickname);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
