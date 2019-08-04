package com.example.yang.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yang.Activity.NewPasswd;
import com.example.yang.Activity.VerifyCodeView;
import com.example.yang.Loger.LocalInfo;
import com.example.yang.network.OkHttpManager;
import com.example.yang.util.SMSCore;
import com.example.yang.util.SMS_Receiver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ForgetPasswd extends Activity implements View.OnClickListener{

    private EditText phone;
    private VerifyCodeView ver;
    private Button getver;
    private Context mContext;
    private Boolean runningThree=false;

    private OkHttpManager http;
    private User user;
    private mMessage mess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpasswd);

        phone = (EditText) findViewById(R.id.forget_phonenumber);
        ver = (VerifyCodeView) findViewById(R.id.forget_verification);
        getver =(Button) findViewById(R.id.forget_getver);
        Button nextstep_button = (Button) findViewById(R.id.forget_nextstep);

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
                //发送获取验证码的信息
                // 注册接收下行receiver

                SMS_Receiver smsReceiver= new SMS_Receiver();
                IntentFilter receiverFilter = new IntentFilter(SMSCore.ACTION_SMS_RECEIVER);
                registerReceiver(smsReceiver, receiverFilter);
                //发送短信
                SMSCore smscore=new SMSCore();
                smscore.SendSMS2("10001", "501", ForgetPasswd.this);
                break;
        }
    }

    public void nextstep() {
        String mobile = phone.getText().toString().trim();
        String verifyCode = ver.getEditContent();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("event", mess.EVENT_FORGET_PASSWD);
        map.put("phone", mobile);
        map.put("verification", verifyCode);

        http.postKeyValuePaires(user.host_url, map, new HttpResponse() {
            @Override
            public void succesd(Call call, Map<String, Object> response) {
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
        LocalInfo info = new LocalInfo();
        if ("".equals(mobile)) {
            Log.e("tag", "mobile=" + mobile);
            Looper.prepare();
            Toast.makeText(ForgetPasswd.this,info.PHONE_IS_NOT_EMMPUT,Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else if (PhoneLogin.isMobileNO(mobile) == false) {
            Looper.prepare();
            Toast.makeText(ForgetPasswd.this,info.PHONE_NUMBER_ERROR,Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else {
            Log.e("tag", "输入了正确的手机号");
            requestVerifyCode(mobile);
        }
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
