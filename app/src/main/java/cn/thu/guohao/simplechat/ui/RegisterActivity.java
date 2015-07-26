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
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.User;


public class RegisterActivity extends ActionBarActivity {

    private EditText mIDEditText;
    private EditText mNameEditText;
    private EditText mPasswordEditText, mConfirmEditText;
    private RadioGroup mSexRadioGroup;
    private Button mRegisterButton;

    private String username, nickname, password, confirm;
    private boolean isMale;
    private User user;

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

                if (checkInput())
                    checkRegister();
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
                login();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(RegisterActivity.this, "Unexpected Register Error", Toast.LENGTH_SHORT).show();
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
