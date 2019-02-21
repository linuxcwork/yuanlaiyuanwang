package com.example.yang.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.tts.loopj.RequestParams;
import com.example.yang.Activity.NewPasswd;
import com.example.yang.Activity.VerifyCodeView;
import com.example.yang.network.OkHttpManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;

public class ForgetPasswd extends Activity implements View.OnClickListener{

    private EditText phone;
    private VerifyCodeView ver;
    private Button getver;
    private Context mContext;
    private Button nextstep_button;
    private Boolean runningThree=false;

    private OkHttpManager http;
    private User user;
    private Message mess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpasswd);

        phone = (EditText) findViewById(R.id.forget_phonenumber);
        ver = (VerifyCodeView) findViewById(R.id.forget_verification);
        getver =(Button) findViewById(R.id.forget_getver);
        nextstep_button = (Button) findViewById(R.id.forget_nextstep);

        getver.setOnClickListener(this);
        nextstep_button.setOnClickListener(this);
        ver.setInputCompleteListener(new VerifyCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                Toast.makeText(ForgetPasswd.this, "inputComplete: " + ver.getEditContent(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void invalidContent() {

            }
        });
        Intent forget = getIntent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.forget_nextstep:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        nextstep();
                    }
                }).start();
                break;
            case R.id.forget_getver:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getMobiile(phone.getText().toString());
                    }
                }).start();
                break;
        }
    }

    public void nextstep() {
        String mobile = phone.getText().toString().trim();
        String verifyCode = ver.getEditContent();
        Map<String,Object> map = new HashMap<String, Object>();
        int id = Preferences.getAccountId();
        String token = Preferences.getToken();
        map.put("user_id", id);
        map.put("event", mess.EVENT_FORGET_PASSWD);
        map.put("phone", mobile);
        map.put("verification", verifyCode);

        http.postKeyValuePaires(user.host_url, map, new HttpResponse() {
            @Override
            public void succesd(Call call, Object response) {
                if (!response.toString().isEmpty()) {

                        if (response.equals("success")) {
                            Intent newpasswd = new Intent(ForgetPasswd.this, NewPasswd.class);
                            startActivity(newpasswd);
                            finish();
                        }else{
                            Toast.makeText(ForgetPasswd.this,"验证码错误",Toast.LENGTH_SHORT).show();
                        }
                }
            }

            @Override
            public void failed(Call call,IOException e) {

            }
        });

    }

    private void requestVerifyCode(String mobile) {
        Map<String,Object> map = new HashMap<String, Object>();
        int id = Preferences.getAccountId();
        map.put("user_id", id);
        map.put("event", mess.EVENT_GET_VERIFICATION);
        map.put(mess.getData(),mobile);

        if (runningThree) {
        } else {
            downTimer.start();
        }

        http.postKeyValuePaires(user.host_url, map, new HttpResponse() {
            @Override
            public void succesd(Call call,Object response) {
                if(!response.toString().isEmpty()) {
                        response.equals("success");
                    Log.e("tag", "获取验证码==" +response);
                }
            }

            @Override
            public void failed(Call call,IOException e) {

            }
        });

    }

    //获取验证码信息，判断是否有手机号码
    private void getMobiile(String mobile) {
        if ("".equals(mobile)) {
            Log.e("tag", "mobile=" + mobile);
            new AlertDialog.Builder(mContext).setTitle("提示").setMessage("手机号码不能为空").setCancelable(true).show();
        } else if (isMobileNO(mobile) == false) {
            new AlertDialog.Builder(mContext).setTitle("提示").setMessage("请输入正确的手机号码").setCancelable(true).show();
        } else {
            Log.e("tag", "输入了正确的手机号");
            requestVerifyCode(mobile);
        }
    }

    private static boolean isMobileNO(String tel) {
        Pattern p = Pattern.compile("^(13[0-9]|15([0-3]|[5-9])|14[5,7,9]|17[1,3,5,6,7,8]|18[0-9])\\d{8}$");
        Matcher m = p.matcher(tel);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    /*
    *  验证码发送后的计时*/
    private CountDownTimer downTimer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long l) {
            runningThree = true;
            getver.setText((l / 1000) + "秒");
        }

        @Override
        public void onFinish() {
            runningThree = false;
            getver.setText("重新发送");
        }
    };

    @Override
    public void onBackPressed() {
        Intent before = new Intent(this,Login.class);
        startActivity(before);
    }
}
