package com.example.yang.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.Activity.VerifyCodeView;
import com.example.yang.network.OkHttpManager;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class PhoneLogin extends Activity implements View.OnClickListener{
    private EditText verifyCodeView;
    private TextView phonenum;
    private Button   getver;
    private Button   login;

    private int countSeconds = 60;//倒计时秒数
    private Context mContext;
    private OkHttpManager http;
    private com.example.yang.myapplication.mMessage mess;
    private User user;
    private Boolean runningThree=false;

    private Handler mCountHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (countSeconds > 0) {
                --countSeconds;
                getver.setText("(" + countSeconds + ")秒后重试");
                mCountHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                countSeconds = 60;
                getver.setText("请重新获取验证码");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonelogin);

        //手机号
        phonenum = (TextView) findViewById(R.id.phone_login_phone);
        getver = (Button) findViewById(R.id.phone_getver);
        login = (Button)  findViewById(R.id.phone_nextstep);
        verifyCodeView = (EditText) findViewById(R.id.phone_verification);
        //头像
        ImageView image = (ImageView) findViewById(R.id.phone_login_image);

        getver.setOnClickListener(this);
        login.setOnClickListener(this);
        /*verifyCodeView.setInputCompleteListener(new VerifyCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                Toast.makeText(PhoneLogin.this, "inputComplete: " + verifyCodeView.getEditContent(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void invalidContent() {

            }
        });*/
        Intent phonelogin = getIntent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.phone_getver:
                startCountBack();
                /** 相信服务器发送获取验证码的请求*/
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getMobiile(phonenum.getText().toString().trim());
                    }
                }).start();*/
                break;
            case R.id.phone_nextstep:
                /*将验证码发送给服务器，获取执行结果，判断是否登陆*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        login();
                    }
                }).start();
                break;
        }
    }

    public void login() {
        String mobile = phonenum.getText().toString().trim();
        String verifyCode = verifyCodeView.getText().toString();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("event", mess.EVENT_PHONE_LOGIN);
        map.put("phone", mobile);
        map.put("verification", verifyCode);

        http.postKeyValuePaires(user.host_url, map, new HttpResponse() {
            @Override
            public void succesd(Call call, Map<String, Object> response) {
                if (!response.toString().isEmpty()) {

                        if (response.equals("success")) {
                            Intent main = new Intent(PhoneLogin.this, MainActivity.class);
                            startActivity(main);
                            finish();
                            }else{
                                Toast.makeText(PhoneLogin.this,"验证码错误",Toast.LENGTH_SHORT).show();
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
        map.put("event", mess.EVENT_GET_VERIFICATION);
        map.put(mess.getData(),mobile);

        if (runningThree) {
        } else {
            downTimer.start();
        }
        http.postKeyValuePaires(user.host_url, map, new HttpResponse() {
            @Override
            public void succesd(Call call,Map<String, Object> response) {
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

    public static boolean isMobileNO(String tel) {
        Pattern p = Pattern.compile("^(13[0-9]|15([0-3]|[5-9])|14[5,7,9]|17[1,3,5,6,7,8]|18[0-9])\\d{8}$");
        Matcher m = p.matcher(tel);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

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

    private void startCountBack() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getver.setText(countSeconds + "");

                mCountHandler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent before = new Intent(this,Login.class);
        startActivity(before);
    }

}
