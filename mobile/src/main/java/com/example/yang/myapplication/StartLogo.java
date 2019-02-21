package com.example.yang.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

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

    private void getMyInfo() {
        Map<String,Object> map = new HashMap<String, Object>();
        RequestParams params = new RequestParams();
        int id = Preferences.getAccountId();
        String token = Preferences.getToken();
        map.put("user_id", id);
        map.put("version", 1.0);
        map.put("app_name","android_jianzhi" );
        map.put("platform", "android_jianzhi");
        map.put("token", token);

        /*http.postKeyValuePaires(user.host_url, map, new HttpResponse() {
            @Override
            public void succesd(Response response) {
                if(response.body() != null) {
                    try {
                        if (response.body().string() == user.getLOGIN()) {
                            nextactivity = "main";
                        }else if(response.body().string() == user.getNOTLOGIN()){
                            nextactivity = "login";
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }*/

          /*  @Override
            public void failed() {

            }
        });

        http.postKeyValuePaires(Constant.API_GET_COMPANY_INFO, params, new AsyncHttpResponseHandler(getApplicationContext(), new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, PreferenceActivity.Header[] headers,
                                  JSONObject response) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getInt("msg_code") == Constant.CODE_SUCCESS) {
                        User user = UserJSONConvert.convertJsonToItem(response.getJSONObject("data"));
                        Toast.make(SplashActivity.this).show(""+user);
                        Preferences.updateAccountUser(user);
                    } else {
                        ToastUtil.make(SplashActivity.this).show(response.getInt("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }));*/
    }
}
