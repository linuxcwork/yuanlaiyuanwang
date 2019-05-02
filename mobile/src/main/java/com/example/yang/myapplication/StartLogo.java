package com.example.yang.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.baidu.tts.loopj.RequestParams;

import java.util.HashMap;
import java.util.Map;



public class StartLogo extends AppCompatActivity {

    private final long SPLASH_LENGTH = 3000;
    private User user;
    private Integer nextactivity;
    private ShareHelper preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View view = View.inflate(this, R.layout.logo, null);
        setContentView(view);

        user = new User();
        preference = new ShareHelper("tet");
        nextactivity = (Integer) preference.get(this, "status", user.getStatus());
        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
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
        if (nextactivity == user.getLOGIN()) {
            Intent main = new Intent(StartLogo.this, MainActivity.class);
            startActivity(main);
            finish();
        } else {
            Intent login = new Intent(StartLogo.this, Login.class);
            startActivity(login);
            finish();
        }

    }
}
