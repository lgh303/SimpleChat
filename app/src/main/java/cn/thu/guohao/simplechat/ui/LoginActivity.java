package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.listener.SaveListener;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.User;


public class LoginActivity extends ActionBarActivity {

    private TextView mRegisterTextView;
    private TextView mSwitchTextView;
    private Button mLoginButton;
    private ProgressBar mProgressBar;
    private EditText mPasswordEditText;
    private String username;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = getIntent().getStringExtra("username");
        nickname = getIntent().getStringExtra("nickname");
        initView();
        initEvent();
    }

    private void initView() {
        mRegisterTextView = (TextView) findViewById(R.id.id_tv_register);
        mSwitchTextView = (TextView) findViewById(R.id.id_tv_login_switch);
        mLoginButton = (Button) findViewById(R.id.id_bt_login);
        mProgressBar = (ProgressBar) findViewById(R.id.id_pb_login);
        mPasswordEditText = (EditText) findViewById(R.id.id_et_login_password);
        TextView mUsernameTextView = (TextView) findViewById(R.id.id_tv_login_username);
        mUsernameTextView.setText(nickname);
    }

    private void initEvent() {
        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        mSwitchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SwitchLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mPasswordEditText.getText().toString();
                mLoginButton.setClickable(false);
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                login(username, password);
            }
        });
    }

    private void login(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.login(LoginActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                mLoginButton.setClickable(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
