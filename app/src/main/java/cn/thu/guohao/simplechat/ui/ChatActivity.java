package cn.thu.guohao.simplechat.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.thu.guohao.simplechat.adapter.ChatItemAdapter;
import cn.thu.guohao.simplechat.adapter.ChatItemBean;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.Conversation;
import cn.thu.guohao.simplechat.data.Message;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.ChatsDAO;
import cn.thu.guohao.simplechat.db.MessageBean;
import cn.thu.guohao.simplechat.db.MessageDAO;


public class ChatActivity extends ActionBarActivity {

    private ListView mListView;
    private ChatItemAdapter mAdapter;
    private ImageView mInputView;
    private ImageView mExtraView;
    private EditText mEditText;
    private String mText = "";
    private List<ChatItemBean> mData = new ArrayList<>();

    private String mFriendUsername, mTitle, mConvID;
    private User mCurrUser;
    private Conversation mCurrConversation;
    private MessageDAO mMessageDAO;
    private Message message;
    private ChatsDAO mChatsDAO;
    private BmobPushManager mPushManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrUser = User.getCurrentUser(this, User.class);
        mFriendUsername = getIntent().getStringExtra("friend_username");
        mConvID = getIntent().getStringExtra("conversationID");
        mTitle = getIntent().getStringExtra("title");
        initConversation();

        mChatsDAO = new ChatsDAO(this, mCurrUser.getUsername());
        mMessageDAO = new MessageDAO(this, mCurrUser.getUsername());
        mMessageDAO.createMessageConvTable(mFriendUsername);

        mPushManager = new BmobPushManager(this);

        setContentView(R.layout.activity_chat);
        initData();
        initView();
        initEvent();
    }

    private void initConversation() {
        BmobQuery<Conversation> query = new BmobQuery<>();
        query.getObject(this, mConvID, new GetListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                mCurrConversation = conversation;
            }

            @Override
            public void onFailure(int i, String s) {
                mCurrConversation = null;
            }
        });
    }

    private void initData() {
        ArrayList<MessageBean> list = mMessageDAO.getMessageFromConvTable(mFriendUsername);
        if (list.isEmpty()) return;
        for (MessageBean message : list) {
            String content = message.getContent();
            ChatItemBean.TYPE type = ChatItemBean.TYPE.LEFT;
            switch (message.getPosType()) {
                case 0:
                    type = ChatItemBean.TYPE.LEFT;
                    break;
                case 1:
                    type = ChatItemBean.TYPE.RIGHT;
                    break;
                case 2:
                    type = ChatItemBean.TYPE.MIDDLE;
                    break;
            }
            mData.add(new ChatItemBean(content, type));
        }
    }

    private void initView() {

        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setTitle(mTitle);
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
                    addChat(mText, ChatItemBean.TYPE.RIGHT);
                    mEditText.setText("");
                }
            }
        });
    }

    private void addChat(String text, ChatItemBean.TYPE type) {
        addChatItem(text, type);
        saveChatItem(text, type);
    }

    private void addChatItem(String text, ChatItemBean.TYPE type) {
        mData.add(new ChatItemBean(text, type));
        mAdapter.notifyDataSetChanged();
    }

    private void saveChatItem(String text, ChatItemBean.TYPE type) {
        int posType = 0, mediaType = 0;
        String speaker = "Notification", uri = "null";
        String update_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (type == ChatItemBean.TYPE.LEFT) {
            posType = 0;
            speaker = mFriendUsername;
        } else if (type == ChatItemBean.TYPE.RIGHT) {
            posType = 1;
            speaker = mCurrUser.getUsername();
        } else if (type == ChatItemBean.TYPE.MIDDLE) {
            posType = 2;
        }
        mMessageDAO.insertMessageToConvTable(mFriendUsername, new MessageBean(
                posType, mediaType, speaker, text, uri, update_time
        ));
        saveChatToServer(speaker, text);
    }

    private void saveChatToServer(String speaker, String text) {
        message = new Message();
        message.setUsername(speaker);
        message.setContent(text);
        message.setConversation(mCurrConversation);
        message.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                mChatsDAO.updateConversation(
                        mFriendUsername,
                        message.getContent(),
                        message.getUpdatedAt()
                );
                BmobRelation messages = new BmobRelation();
                messages.add(message);
                mCurrConversation.setMessages(messages);
                mCurrConversation.setLatestMessage(message.getContent());
                mCurrConversation.update(ChatActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        mPushManager.pushMessageAll(message.getContent());
                        // TODO push message or save it to user's unread list.
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
