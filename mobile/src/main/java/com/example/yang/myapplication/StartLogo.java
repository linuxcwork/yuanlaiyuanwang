package com.example.yang.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.example.yang.util.SharedPreferencedUtils;
import com.example.yang.util.XmppConnection;


public class StartLogo extends AppCompatActivity {

    private final long SPLASH_LENGTH = 3000;
    private User user;
    private Integer nextactivity= 0;
    private SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View view = View.inflate(this, R.layout.logo, null);
        setContentView(view);
        //ChatSDKInit chatSDKInit = new ChatSDKInit(this);
        user = new User();
        nextactivity =  Integer.parseInt(sharedPreferencedUtils.getUserInfo(this, "status", "0"));
        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(10000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                Activitychoice();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });

    }

    public void Activitychoice(){
        XmppConnection xmppConnection = XmppConnection.getInstance();
        if (xmppConnection.islogin()) {
            Intent login = new Intent(StartLogo.this, Login.class);
            startActivity(login);
            finish();
        } else {
            Intent main = new Intent(StartLogo.this, MainActivity.class);
            startActivity(main);
            finish();
        }
    }
}
