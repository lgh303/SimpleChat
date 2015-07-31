package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.listener.UpdateListener;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.UserDAO;

public class EditActivity extends ActionBarActivity {

    private EditText mNameEditText;
    private Button mNameButton;
    private String mText;

    private User mCurrUser;
    private UserDAO mUserDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mCurrUser = User.getCurrentUser(this, User.class);
        mNameEditText = (EditText) findViewById(R.id.id_et_edit_name);
        mNameButton = (Button) findViewById(R.id.id_bt_edit_name);
        mNameEditText.setText(mCurrUser.getNickname());
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                mText = mNameEditText.getText().toString();
                if (mText.isEmpty() || mText.equals(mCurrUser.getNickname()))
                    mNameButton.setClickable(false);
                else
                    mNameButton.setClickable(true);
            }
        });
        mNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("nickname", mText);
                String gender = getString(R.string.hint_female);
                if (mCurrUser.getIsMale())
                    gender = getString(R.string.hint_male);
                intent.putExtra("gender", gender);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
