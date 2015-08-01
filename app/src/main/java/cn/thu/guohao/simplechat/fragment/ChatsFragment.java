package cn.thu.guohao.simplechat.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.thu.guohao.simplechat.adapter.ChatBean;
import cn.thu.guohao.simplechat.adapter.ChatsAdapter;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.Conversation;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.ChatsDAO;
import cn.thu.guohao.simplechat.db.ConversationBean;
import cn.thu.guohao.simplechat.receiver.MessageReceiver;
import cn.thu.guohao.simplechat.ui.ChatActivity;
import cn.thu.guohao.simplechat.util.InfoPack;
import cn.thu.guohao.simplechat.util.PackProcessor;
import cn.thu.guohao.simplechat.util.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment
        implements AdapterView.OnItemClickListener{

    private ListView mListView;
    private List<ChatBean> mChatBeans;

    private OnFragmentInteractionListener mListener;

    private User mCurrUser;
    private ChatsAdapter mAdapter;
    private ChatsDAO mChatsDAO;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PackProcessor.FORWARD_ACTION)){
                //String jsonString = intent.getStringExtra(
                //        PackProcessor.FORWARD_MESSAGE);
                //InfoPack pack = Utils.parseMessage(jsonString);
                InfoPack pack = (InfoPack) intent.getSerializableExtra(
                        PackProcessor.FORWARD_MESSAGE
                );
                if (pack.getType() == InfoPack.TYPE.MESSAGE) {
                    for (ChatBean chatBean : mChatBeans)
                        if (pack.getSender().equals(chatBean.username)) {
                            chatBean.unread = chatBean.unread + 1;
                            chatBean.content = pack.getContent();
                            chatBean.time = formatTime(pack.getUpdate_time());
                            mChatBeans.remove(chatBean);
                            mChatBeans.add(0, chatBean);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    // TODO what if this is a new group chat
                }
            }
        }
    };

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        mListView = (ListView) view.findViewById(R.id.id_lv_chats);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrUser = User.getCurrentUser(getActivity(), User.class);
        mChatsDAO = new ChatsDAO(getActivity(), mCurrUser.getUsername());
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PackProcessor.FORWARD_ACTION);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    private void initData() {
        mChatBeans = new LinkedList<>();
        ArrayList<ConversationBean> list = mChatsDAO.getConversation();
        if (list.isEmpty())
            initDataViaCloud();
        else {
            for (ConversationBean conv : list) {
                mChatBeans.add(new ChatBean(
                        conv.getId(), conv.getFriend_username(),
                        conv.getTitle(), conv.getLatestMessage(),
                        conv.getUnreadCount(),
                        formatTime(conv.getUpdate_time()),
                        conv.getUri()
                ));
            }
            mAdapter = new ChatsAdapter(getActivity(), mChatBeans, mListView);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(ChatsFragment.this);
        }
    }

    private String formatTime(String update_time) {
        String curr_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (update_time.substring(0, 10).equals(curr_time.substring(0, 10)))
            return update_time.substring(11, 16);
        else
            return update_time.substring(5, 10);
    }

    private void initDataViaCloud() {
        BmobQuery<Conversation> query = new BmobQuery<>();
        query.addWhereRelatedTo("conversations", new BmobPointer(mCurrUser));
        query.findObjects(getActivity(), new FindListener<Conversation>() {
            @Override
            public void onSuccess(List<Conversation> list) {
                for (Conversation conv : list) {
                    User aUser = conv.getaUser();
                    String title, friend_username, uri;
                    if (aUser.getObjectId().equals(mCurrUser.getObjectId())) {
                        title = conv.getbNickname();
                        friend_username = conv.getbUsername();
                        uri = conv.getbUri();
                    } else {
                        title = conv.getaNickname();
                        friend_username = conv.getaUsername();
                        uri = conv.getaUri();
                    }
                    mChatBeans.add(new ChatBean(
                            conv.getObjectId(), friend_username,
                            title, conv.getLatestMessage(), conv.getUnread(),
                            formatTime(conv.getUpdatedAt()), uri)
                    );
                    mChatsDAO.insertConversation(new ConversationBean(
                            conv.getObjectId(),
                            title, friend_username,
                            conv.getUnread(),
                            conv.getLatestMessage(),
                            conv.getUpdatedAt(), uri));
                }
                mAdapter = new ChatsAdapter(getActivity(), mChatBeans, mListView);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(ChatsFragment.this);
            }
            @Override
            public void onError(int i, String s) {  }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("friend_username", mChatBeans.get(position).username);
        intent.putExtra("title", mChatBeans.get(position).title);
        intent.putExtra("conversationID", mChatBeans.get(position).convID);
        intent.putExtra("uri", mChatBeans.get(position).uri);
        startActivity(intent);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
