package com.example.yang.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yang.myapplication.ForgetPasswd;
import com.example.yang.myapplication.HttpResponse;
import com.example.yang.myapplication.Login;
import com.example.yang.myapplication.MainActivity;
import com.example.yang.myapplication.Message;
import com.example.yang.myapplication.Preferences;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.User;
import com.example.yang.network.OkHttpManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class NewPasswd extends Activity {
    private EditText passwd;
    private EditText passwd_check;
    private Button nextstep;
    private Message mess;
    private OkHttpManager http;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpasswd_newpasswd);
        Intent newpasswd = getIntent();

        passwd = (EditText) findViewById(R.id.newpasswd_passwd);
        passwd_check =(EditText) findViewById(R.id.newpasswd_passwd_check);
        nextstep = (Button) findViewById(R.id.newpasswd_nextstep);

        nextstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.newpasswd_nextstep:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(passwd.getText().equals(passwd_check.getText())){
                                    String passwd_send = passwd.getText().toString();
                                    Map<String,Object> map = new HashMap<String, Object>();
                                    int id = Preferences.getAccountId();
                                    String token = Preferences.getToken();
                                    map.put("user_id", id);
                                    map.put("event", mess.Event_PASSWD_CHANGE);
                                    map.put("passwd", passwd);

                                    http.postKeyValuePaires(user.host_url, map, new HttpResponse() {
                                        @Override
                                        public void succesd(Call call, Object response) {
                                            if (!response.toString().isEmpty()) {
                                                    if (response.equals("success")) {
                                                        Intent newpasswd = new Intent(NewPasswd.this, MainActivity.class);
                                                        startActivity(newpasswd);
                                                        finish();
                                                    }else{
                                                        Toast.makeText(NewPasswd.this,"验证码错误",Toast.LENGTH_SHORT).show();
                                                    }

                                            }
                                        }

                                        @Override
                                        public void failed(Call call,IOException e) {

                                        }
                                    });
                                }
                            }
                        }).start();
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent before = new Intent(this,ForgetPasswd.class);
        startActivity(before);
    }
}
