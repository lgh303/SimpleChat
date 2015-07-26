package cn.thu.guohao.simplechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
        SharedPreferences pref = getSharedPreferences("latest_user", MODE_PRIVATE);
        final String objectID = pref.getString("objectID", null);
        final String username = pref.getString("username", null);
        final String nickname = pref.getString("nickname", null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currUser == null) {
                    if (objectID == null) {
                        Intent intent = new Intent(SplashActivity.this, SwitchLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra("username", username);
                        intent.putExtra("nickname", nickname);
                        startActivity(intent);
                    }

                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }, DELAY_LENGTH);



    }
}
