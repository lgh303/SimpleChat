package cn.thu.guohao.simplechat.ui;

import android.content.Intent;
import android.net.Uri;
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

import cn.thu.guohao.simplechat.data.User;
import cn.thu.guohao.simplechat.fragment.ChatsFragment;
import cn.thu.guohao.simplechat.fragment.ContactsFragment;
import cn.thu.guohao.simplechat.fragment.DiscoverFragment;
import cn.thu.guohao.simplechat.fragment.MeFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        User currUser = User.getCurrentUser(this, User.class);
        Log.i("lgh", "MainActivity: currUser=" + currUser);
        initView();
        initEvent();
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
                    case 0:
                        return mChatsFragment;
                    case 1:
                        return mContactsFragment;
                    case 2:
                        return mDiscoverFragment;
                    case 3:
                        return mMeFragment;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void logout() {
        User.logOut(this);
        Intent intent = new Intent(MainActivity.this, SwitchLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
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
