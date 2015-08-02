package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.thu.guohao.simplechat.data.Delivery;
import cn.thu.guohao.simplechat.data.Installation;
import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.fragment.ChatsFragment;
import cn.thu.guohao.simplechat.fragment.ContactsFragment;
import cn.thu.guohao.simplechat.fragment.DiscoverFragment;
import cn.thu.guohao.simplechat.fragment.MeFragment;
import cn.thu.guohao.simplechat.util.InfoPack;
import cn.thu.guohao.simplechat.util.PackProcessor;
import cn.thu.guohao.simplechat.util.Utils;
import cn.thu.guohao.simplechat.view.PagerIcon;
import cn.thu.guohao.simplechat.R;


public class MainActivity extends ActionBarActivity
        implements ChatsFragment.OnFragmentInteractionListener,
        ContactsFragment.OnFragmentInteractionListener,
        DiscoverFragment.OnFragmentInteractionListener,
        MeFragment.OnFragmentInteractionListener,
        View.OnClickListener,
        ViewPager.OnPageChangeListener {

    public ViewPager mViewPager;
    private FragmentPagerAdapter mPagerAdapter;
    private ChatsFragment mChatsFragment;
    private ContactsFragment mContactsFragment;
    private DiscoverFragment mDiscoverFragment;
    private MeFragment mMeFragment;

    private List<PagerIcon> mPagerIcons = new ArrayList<>();

    private User mCurrUser;
    private Installation mInstallation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurrUser = User.getCurrentUser(this, User.class);
        initInstallation();
        initView();
        initEvent();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                receiveWaitingMessage();
                return null;
            }
        }.execute();
    }

    private void initInstallation() {
        BmobQuery<Installation> query = new BmobQuery<>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(this));
        query.findObjects(this, new FindListener<Installation>() {
            @Override
            public void onSuccess(List<Installation> list) {
                if (list.isEmpty()) {
                    Installation install = new Installation(MainActivity.this);
                    install.setInstallationId(BmobInstallation.getInstallationId(MainActivity.this));
                    install.setUsername(mCurrUser.getUsername());
                    install.save(MainActivity.this);
                } else {
                    mInstallation = list.get(0);
                    mInstallation.setUsername(mCurrUser.getUsername());
                    mInstallation.update(MainActivity.this);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.i("lgh", "find failed");
            }
        });
        BmobPush.startWork(this, "fc26b418ba0a8938a58eb1ff46976026");
    }

    private void initView()
    {
        mPagerIcons.add((PagerIcon) findViewById(R.id.id_pi_chats));
        mPagerIcons.add((PagerIcon) findViewById(R.id.id_pi_contacts));
        mPagerIcons.add((PagerIcon) findViewById(R.id.id_pi_discover));
        mPagerIcons.add((PagerIcon) findViewById(R.id.id_pi_me));
        mPagerIcons.get(0).setIconAlpha(1.0f);

        mChatsFragment = new ChatsFragment();
        mContactsFragment = new ContactsFragment();
        mDiscoverFragment = new DiscoverFragment();
        mMeFragment = new MeFragment();

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                switch (i) {
                    case 0: return mChatsFragment;
                    case 1: return mContactsFragment;
                    case 2: return mDiscoverFragment;
                    case 3: return mMeFragment;
                }
                return null;
            }
            @Override
            public int getCount() {
                return 4;
            }
        };
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initEvent() {
        for (PagerIcon pi : mPagerIcons) {
            pi.setOnClickListener(this);
        }
        mViewPager.addOnPageChangeListener(this);
    }

    public void receiveWaitingMessage() {
        BmobQuery<Delivery> query = new BmobQuery<>();
        query.addWhereEqualTo("receiver", mCurrUser.getUsername());
        query.findObjects(this, new FindListener<Delivery>() {
            @Override
            public void onSuccess(List<Delivery> list) {
                if (list.isEmpty()) {
                    Log.i("lgh", "No Waiting Delivery");
                } else {
                    for (Delivery delivery : list) {
                        final InfoPack pack = Utils.parseMessage(delivery.getJson());
                        PackProcessor.getInstance(MainActivity.this).processPack(pack);
                        delivery.delete(MainActivity.this, new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                Log.i("lgh", "Delete Delivery from server: " + pack.toString());
                            }
                            @Override
                            public void onFailure(int i, String s) {}
                        });
                    }
                }
            }
            @Override
            public void onError(int i, String s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_group_chat:
                break;
            case R.id.menu_add_friend:
                Intent intent = new Intent(this, SearchUsersActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_scan:
                break;
            case R.id.menu_feedback:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void logout() {
        saveLatestUser();
        String username = mCurrUser.getUsername();
        String nickname = mCurrUser.getNickname();
        User.logOut(this);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("username", username);
        intent.putExtra("nickname", nickname);
        startActivity(intent);
    }

    private void saveLatestUser() {
        SharedPreferences pref = getSharedPreferences("latest_user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", mCurrUser.getUsername());
        editor.putString("objectID", mCurrUser.getObjectId());
        editor.putString("nickname", mCurrUser.getNickname());
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_pi_chats:
                switchToPagerIcon(0);
                break;
            case R.id.id_pi_contacts:
                switchToPagerIcon(1);
                break;
            case R.id.id_pi_discover:
                switchToPagerIcon(2);
                break;
            case R.id.id_pi_me:
                switchToPagerIcon(3);
                break;
        }
    }

    private void switchToPagerIcon(int i) {
        for (PagerIcon pi: mPagerIcons)
            pi.setIconAlpha(0);
        mPagerIcons.get(i).setIconAlpha(1.0f);
        mViewPager.setCurrentItem(i, false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            mPagerIcons.get(position).setIconAlpha(1 - positionOffset);
            mPagerIcons.get(position + 1).setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
