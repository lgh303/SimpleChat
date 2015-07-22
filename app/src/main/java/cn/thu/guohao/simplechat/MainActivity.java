package cn.thu.guohao.simplechat;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity
        implements ChatsFragment.OnFragmentInteractionListener,
        View.OnClickListener {

    public ViewPager mViewPager;
    private FragmentPagerAdapter mPagerAdapter;
    private List<Fragment> mTabs = new ArrayList<>();
    private String[] mTitles = new String[] {
            "Chats",
            "Contacts",
            "Discover",
            "Me",
    };

    private List<PagerIcon> mPagerIcons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        for (String title : mTitles) {
            ChatsFragment frag = ChatsFragment.newInstance(title);
            mTabs.add(frag);
        }

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mTabs.get(i);
            }

            @Override
            public int getCount() {
                return mTabs.size();
            }
        };

        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initEvent() {
        for (PagerIcon pi : mPagerIcons) {
            pi.setOnClickListener(this);
        }
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
}
