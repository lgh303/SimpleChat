package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.thu.guohao.simplechat.R;


public class SwitchLoginActivity extends ActionBarActivity {

    private EditText mNameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private TextView mRegisterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_login);
        initView();
        initEvent();
    }

    private void initView() {
        mLoginButton = (Button) findViewById(R.id.id_bt_login_switch);
        mNameEditText = (EditText) findViewById(R.id.id_et_switch_username);
        mPasswordEditText = (EditText) findViewById(R.id.id_et_switch_password);
        mRegisterTextView = (TextView) findViewById(R.id.id_tv_switch_register);
    }

    private void initEvent() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mNameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                Log.i("lgh", "username: " + username + "; password: " + password);
                Intent intent = new Intent(SwitchLoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SwitchLoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_switch_login, menu);
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
