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
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.adapter.ContactAdapter;
import cn.thu.guohao.simplechat.adapter.ContactBean;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.db.UserBean;
import cn.thu.guohao.simplechat.db.UserDAO;
import cn.thu.guohao.simplechat.ui.ProfileActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ContactsFragment extends Fragment
        implements AdapterView.OnItemClickListener{

    private ListView mListView;
    private List<ContactBean> mData;
    private User mCurrUser;
    private ContactAdapter mAdapter;

    private OnFragmentInteractionListener mListener;

    private UserDAO mUserDAO;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        mListView = (ListView) view.findViewById(R.id.id_lv_contacts);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrUser = User.getCurrentUser(getActivity(), User.class);
        mUserDAO = new UserDAO(getActivity(), mCurrUser.getUsername());
        initData();
    }

    private void initData() {
        mData = new ArrayList<>();
        ArrayList<UserBean> list = mUserDAO.get();
        if (list.isEmpty())
            initDataViaCloud();
        else {
            for (UserBean user : list) {
                mData.add(new ContactBean(user.getUsername(), user.getNickname()));
            }
            mAdapter = new ContactAdapter(getActivity(), mData, mListView);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(ContactsFragment.this);
        }
    }

    private void initDataViaCloud() {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("friends", new BmobPointer(mCurrUser));
        query.findObjects(getActivity(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                for (User user : list) {
                    mData.add(new ContactBean(user.getUsername(), user.getNickname()));
                    int sex = 0;
                    if (user.getIsMale()) sex = 1;
                    int type = 0;
                    mUserDAO.insert(new UserBean(
                            user.getUsername(), user.getNickname(),
                            sex, type, user.getPhotoUri()));
                }
                mAdapter = new ContactAdapter(getActivity(), mData, mListView);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(ContactsFragment.this);
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
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra("currUser", mCurrUser.getUsername());
        intent.putExtra("username", mData.get(position).username);
        startActivity(intent);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
