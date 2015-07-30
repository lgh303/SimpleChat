package cn.thu.guohao.simplechat.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.thu.guohao.simplechat.adapter.ChatItemAdapter;
import cn.thu.guohao.simplechat.adapter.ChatItemBean;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.Conversation;
import cn.thu.guohao.simplechat.data.Installation;
import cn.thu.guohao.simplechat.data.Message;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.ChatsDAO;
import cn.thu.guohao.simplechat.db.ConversationBean;
import cn.thu.guohao.simplechat.db.MessageBean;
import cn.thu.guohao.simplechat.db.MessageDAO;
import cn.thu.guohao.simplechat.receiver.MessageReceiver;
import cn.thu.guohao.simplechat.util.InfoPack;
import cn.thu.guohao.simplechat.util.Utils;


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
    private Installation mFriendInstallation = null;

    private Message message;
    private MessageDAO mMessageDAO;
    private ChatsDAO mChatsDAO;

    private BmobPushManager<Installation> mPushManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrUser = User.getCurrentUser(this, User.class);
        mFriendUsername = getIntent().getStringExtra("friend_username");
        mConvID = getIntent().getStringExtra("conversationID");
        mTitle = getIntent().getStringExtra("title");
        initConversation();
        initInstallation();

        mChatsDAO = new ChatsDAO(this, mCurrUser.getUsername());
        mMessageDAO = new MessageDAO(this, mCurrUser.getUsername());
        mMessageDAO.createMessageConvTable(mFriendUsername);

        mPushManager = new BmobPushManager<>(this);

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

    private void initInstallation() {
        BmobQuery<Installation> query = new BmobQuery<>();
        query.addWhereEqualTo("username", mFriendUsername);
        query.findObjects(this, new FindListener<Installation>() {
            @Override
            public void onSuccess(List<Installation> list) {
                if (!list.isEmpty())
                    mFriendInstallation = list.get(0);
            }

            @Override
            public void onError(int i, String s) {
            }
        });
    }

    private void initData() {
        mMessageDAO.markMessageReadInConvTable(mFriendUsername);
        mChatsDAO.clearUnread(mFriendUsername);
        ArrayList<MessageBean> list = mMessageDAO.getMessageFromConvTable(mFriendUsername, false);
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
        mListView.setSelection(mListView.getCount() - 1);
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
                    String update_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    addChat(mText, ChatItemBean.TYPE.RIGHT, update_time);
                    mEditText.setText("");
                }
            }
        });
    }

    private void addChat(String text, ChatItemBean.TYPE type, String update_time) {
        addChatItem(text, type);
        saveLocal(text, type, update_time);
        saveMessageRemote(text, type, update_time);
    }

    private void addChatItem(String text, ChatItemBean.TYPE type) {
        mData.add(new ChatItemBean(text, type));
        mAdapter.notifyDataSetChanged();
    }

    private void saveLocal(String text, final ChatItemBean.TYPE type, String update_time) {
        int posType = 0, mediaType = 0;
        String speaker = "Notification", uri = "null";

        if (type == ChatItemBean.TYPE.LEFT) {
            posType = 0;
            speaker = mFriendUsername;
        } else if (type == ChatItemBean.TYPE.RIGHT) {
            posType = 1;
            speaker = mCurrUser.getUsername();
        } else if (type == ChatItemBean.TYPE.MIDDLE) {
            posType = 2;
        }
        mMessageDAO.insertMessageToConvTable(
                mFriendUsername,
                new MessageBean(
                        posType, mediaType, speaker,
                        text, uri, update_time
                ),
                false
        );
        mChatsDAO.updateConversation(
                mFriendUsername,
                text,
                update_time,
                0
        );
    }

    private void saveMessageRemote(String text, final ChatItemBean.TYPE type, final String update_time) {
        String speaker = "Notification";
        if (type == ChatItemBean.TYPE.LEFT)
            speaker = mFriendUsername;
        else if (type == ChatItemBean.TYPE.RIGHT)
            speaker = mCurrUser.getUsername();
        message = new Message();
        message.setUsername(speaker);
        message.setContent(text);
        message.setConversation(mCurrConversation);
        message.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                updateConversationRemote(type, update_time);
            }
            @Override
            public void onFailure(int i, String s) { }
        });
    }

    private void updateConversationRemote(final ChatItemBean.TYPE type, final String update_time) {
        final BmobRelation messages = new BmobRelation();
        messages.add(message);
        mCurrConversation.setMessages(messages);
        mCurrConversation.setLatestMessage(message.getContent());
        mCurrConversation.setUnread(0);
        mCurrConversation.update(ChatActivity.this, new UpdateListener() {
            @Override
            public void onSuccess() {
                if (type != ChatItemBean.TYPE.RIGHT) return;
                if (message.getUsername().equals("filehelper")) return;
                sendMessage(update_time);
            }
            @Override
            public void onFailure(int i, String s) { }
        });
    }

    private void sendMessage(final String update_time) {
        if (mFriendInstallation == null) {
            // TODO save it to user's wait list
        } else {
            BmobQuery<Installation> query = Installation.getQuery();
            query.addWhereEqualTo("username", mFriendUsername);
            mPushManager.setQuery(query);
            try {
                JSONObject json = new JSONObject("{" +
                        "\"type\":" + "\"" + "message" + "\"" + "," +
                        "\"sender\":" + "\"" + mCurrUser.getUsername() + "\"" + "," +
                        "\"content\":" + "\"" + message.getContent() + "\"" + "," +
                        "\"uri\":" + "\"" + "null" + "\"" + "," +
                        "\"update_time\":" + "\"" + update_time + "\"" +
                        "}");
                mPushManager.pushMessage(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MessageReceiver.FORWARD_ACTION)){
                String jsonString = intent.getStringExtra(
                        MessageReceiver.FORWARD_MESSAGE);
                InfoPack pack = Utils.parseMessage(jsonString);
                if (pack.getType() == InfoPack.TYPE.MESSAGE &&
                        mFriendUsername.equals(pack.getSender())) {
                    addChatItem(pack.getContent(), ChatItemBean.TYPE.LEFT);
                    mChatsDAO.clearUnread(mFriendUsername);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MessageReceiver.FORWARD_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
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
