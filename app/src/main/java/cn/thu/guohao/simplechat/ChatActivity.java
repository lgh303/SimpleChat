package cn.thu.guohao.simplechat;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends ActionBarActivity {

    private ListView mListView;
    private ChatItemAdapter mAdapter;
    private ImageView mInputView;
    private ImageView mExtraView;
    private EditText mEditText;
    private String mText = "";
    private List<ChatItemBean> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setTitle(title);
        setContentView(R.layout.activity_chat);
        initView();
        initEvent();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.id_lv_chat_pane);
        mInputView = (ImageView) findViewById(R.id.id_iv_chat_input);
        mEditText = (EditText) findViewById(R.id.id_et_chat);
        mExtraView = (ImageView) findViewById(R.id.id_iv_chat_extras);
        mAdapter = new ChatItemAdapter(this, mData, mListView);
        mListView.setAdapter(mAdapter);
    }

    private void initEvent() {
        mInputView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mEditText.getText().toString();
                if (mText.length() > 0 && text.length() == 0)
                    mExtraView.setImageResource(R.drawable.selector_chat_extras);
                else if (mText.length() == 0 && text.length() > 0)
                    mExtraView.setImageResource(R.drawable.selector_chat_send);
                mText = text;
            }
        });
        mExtraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mText.length() > 0) {
                    Log.i("lgh", "Send: " + mText);
                    mData.add(new ChatItemBean(mText));
                    mAdapter.notifyDataSetChanged();
                    mEditText.setText("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
