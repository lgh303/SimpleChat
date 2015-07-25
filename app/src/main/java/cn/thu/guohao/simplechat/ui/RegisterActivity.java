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
import android.widget.Toast;

import cn.thu.guohao.simplechat.R;


public class RegisterActivity extends ActionBarActivity {

    private EditText mNameEditText;
    private EditText mPasswordEditText, mConfirmEditText;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initEvent();
    }

    private void initView() {
        mNameEditText = (EditText) findViewById(R.id.id_et_reg_username);
        mPasswordEditText = (EditText) findViewById(R.id.id_et_reg_password);
        mConfirmEditText = (EditText) findViewById(R.id.id_et_reg_confirm);
        mRegisterButton = (Button) findViewById(R.id.id_bt_register);
    }

    private void initEvent() {
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mNameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String confirm = mConfirmEditText.getText().toString();
                if (username.isEmpty() || password.isEmpty() || !password.equals(confirm)) {
                    Toast.makeText(RegisterActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("lgh", "register: " + username + " " + password);
                Intent intent = new Intent(RegisterActivity.this, RegisterSuccessActivity.class);
                intent.putExtra("username", username);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
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
