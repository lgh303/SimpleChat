package cn.thu.guohao.simplechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import cn.bmob.v3.Bmob;
import cn.thu.guohao.simplechat.R;
import cn.thu.guohao.simplechat.data.User;


public class SplashActivity extends Activity {

    private final int DELAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Bmob.initialize(this, "fc26b418ba0a8938a58eb1ff46976026");
        final User currUser = User.getCurrentUser(this, User.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currUser == null) {
                    Intent intent = new Intent(SplashActivity.this, SwitchLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }, DELAY_LENGTH);



    }
}
