package com.example.yang.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.yang.myapplication.HttpResponse;
import com.example.yang.myapplication.Login;
import com.example.yang.myapplication.R;
import com.example.yang.network.OkHttpManager;
import com.example.yang.util.UrlListdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.Activity
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/8/4 15:52
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class SettingActivity extends Activity implements View.OnClickListener {

    private final String TAG = "SettingActivity";
    private OkHttpManager http = new OkHttpManager();
    private UrlListdb urlListdb = new UrlListdb();

    private final int RESPONSE = 1;
    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RESPONSE:
                        Map<String, Object> response = (Map<String, Object>) msg.obj;
                        logoutRsp(response);
                        break;
                }
                super.handleMessage(msg);
            }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //布局文件 setting_main
        setContentView(R.layout.setting_main);
        RelativeLayout relativeLayout = findViewById(R.id.exit_login);
        relativeLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        System.out.println("dddddddddddrrrrrrrrrrrrrrrrrr");
        switch (v.getId()){
            case R.id.exit_login:
                System.out.println("ddddddddddddddddddddd");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("id", sharedPreferences.getString("id", ""));
                        map.put("telphone", sharedPreferences.getString("telphone", ""));
                        http.postKeyValuePaires(urlListdb.logout, map, new HttpResponse() {
                            @Override
                            public void succesd(Call call, Map<String, Object> response) {
                                Message message = new Message();
                                message.what = RESPONSE;
                                message.obj = response;
                                mhandler.sendMessage(message);
                            }

                            @Override
                            public void failed(Call call, IOException e) {
                                System.out.println(e + call.request().toString());
                            }
                        });
                    }
                }).start();
                break;
            default:
                Log.e(TAG,"NO ID");

        }
    }

    public void logoutRsp(Map<String, Object> response){
        int count = 0;
        System.out.println("dfdsfsfsdfdsf"+response);
        if(response.get("count") != null){
            count = (int)response.get("count");
        }
        if(count > 0) {
            SharedPreferences sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("state","0");
            editor.commit();
        }
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
