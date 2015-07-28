package cn.thu.guohao.simplechat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

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
import cn.thu.guohao.simplechat.ui.ChatActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment
        implements AdapterView.OnItemClickListener{
    private static final String TITLE = "ChatsFragment.TITLE";

    private ListView mListView;
    private List<ChatBean> mChatBeans;

    private OnFragmentInteractionListener mListener;

    private User mCurrUser;
    private ChatsAdapter mAdapter;
    private ChatsDAO mChatsDAO;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @return A new instance of fragment ChatsFragment.
     */
    public static ChatsFragment newInstance(String title) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

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
        initData();
    }

    private void initData() {
        mChatBeans = new ArrayList<>();
        ArrayList<ConversationBean> list = mChatsDAO.getConversation();
        if (list.isEmpty())
            initDataViaCloud();
        else {
            for (ConversationBean conv : list) {
                mChatBeans.add(new ChatBean(conv.getTitle(), conv.getLatestMessage()));
            }
            mAdapter = new ChatsAdapter(getActivity(), mChatBeans, mListView);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(ChatsFragment.this);
        }
    }

    private void initDataViaCloud() {
        BmobQuery<Conversation> query = new BmobQuery<>();
        query.addWhereRelatedTo("conversations", new BmobPointer(mCurrUser));
        query.findObjects(getActivity(), new FindListener<Conversation>() {
            @Override
            public void onSuccess(List<Conversation> list) {
                for (Conversation conv : list) {
                    User aUser = conv.getaUser();
                    String title, friend_username;
                    if (aUser.getObjectId().equals(mCurrUser.getObjectId())) {
                        title = conv.getbNickname();
                        friend_username = conv.getbUsername();
                    } else {
                        title = conv.getaNickname();
                        friend_username = conv.getaUsername();
                    }
                    mChatBeans.add(new ChatBean(title, conv.getLatestMessage()));
                    mChatsDAO.insertConversation(new ConversationBean(
                            title, friend_username,
                            conv.getLatestMessage(),
                            conv.getUpdatedAt()));
                }
                mAdapter = new ChatsAdapter(getActivity(), mChatBeans, mListView);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(ChatsFragment.this);
            }
            @Override
            public void onError(int i, String s) {

            }
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
        intent.putExtra("title", mChatBeans.get(position).title);
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
