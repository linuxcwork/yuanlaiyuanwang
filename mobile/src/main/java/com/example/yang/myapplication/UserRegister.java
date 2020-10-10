package com.example.yang.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yang.Activity.VerifyCodeView;
import com.example.yang.Loger.LocalInfo;
import com.example.yang.network.OkHttpManager;
import com.example.yang.network.ServerResponse;
import com.example.yang.util.SharedPreferencedUtils;
import com.example.yang.util.UrlListdb;
import com.example.yang.util.XmppConnection;


import java.util.HashMap;
import java.util.Map;

public class UserRegister extends Activity implements View.OnClickListener{
    private EditText account;
    private EditText passwd1;
    private EditText phone;
    private VerifyCodeView ver;
    private Button  getver;
    private Button reg;
    private final String TAG = "UserRegister";

    private mMessage mess = new mMessage();
    private User user;
    private Context mContext;
    private OkHttpManager http  = new OkHttpManager();

    private ServerResponse serverResponse = new ServerResponse();
    private Boolean runningThree = false;
    private LocalInfo info = new LocalInfo();
    private UrlListdb urlListdb = new UrlListdb();
    private final int REGRESPONSE = 1;

    private SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();

    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGRESPONSE:
                    String mstr = (String) msg.obj;
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        view_init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

       /*     case R.id.register_getver:
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
                smscore.SendSMS2("10001", "501", UserRegister.this);
                break;*/
            case R.id.register_button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        register();
                    }
                }).start();
                break;
                default:
                    System.out.println("UserRegister no match id");
        }
    }

    private void register(){
        if(info_check()){
            return;
        }
        String useraccount = account.getText().toString().trim();
        String passwd = passwd1.getText().toString();
        String mobile = phone.getText().toString();
        String ver_send = ver.getEditContent();

        if(!PhoneLogin.isMobileNO(mobile)){
            System.out.println("手机号错误");
            return;
        }


        Map<String,String> map = new HashMap<String, String>();
        map.put("account", useraccount);
       // map.put("event", mess.EVENT_REGISTER);
        //map.put("phone", mobile);
        //map.put("verification", ver_send);
        map.put("password",passwd);

        XmppConnection xmppConnection = XmppConnection.getInstance();
        boolean ret = false;

    }

    private void view_init(){
        account = (EditText) findViewById(R.id.register_acount);
        passwd1 = (EditText) findViewById(R.id.register_passwd);
        phone = (EditText) findViewById(R.id.register_phonenumber);
     /*   ver = (VerifyCodeView) findViewById(R.id.register_verify_code_view);
        getver = (Button) findViewById(R.id.register_getver);*/
        reg = (Button) findViewById(R.id.register_button);

        getver.setOnClickListener(this);
        reg.setOnClickListener(this);
        ver.setInputCompleteListener(new VerifyCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                Toast.makeText(UserRegister.this, "inputComplete: " + ver.getEditContent(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void invalidContent() {

            }
        });

    }

    private void requestVerifyCode(String mobile) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("verifycode",mobile);

        if (runningThree) {
        } else {
            downTimer.start();
        }

        http.postKeyValuePaires(urlListdb.register, map, null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //获取验证码信息，判断是否有手机号码
    private void getMobiile(String mobile) {
        if ("".equals(mobile)) {
            Looper.prepare();
            Toast.makeText(UserRegister.this,info.PHONE_IS_NOT_EMMPUT,Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else if (PhoneLogin.isMobileNO(mobile) == false) {
            Looper.prepare();
            Toast.makeText(UserRegister.this,info.PHONE_NUMBER_ERROR,Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else {
            Log.e("tag", "输入了正确的手机号");
            requestVerifyCode(mobile);
        }
    }

    private void startCountBack() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //getver.setText(countSeconds + "");

                //mCountHandler.sendEmptyMessage(0);
            }
        });
    }

   /**************************************************************************
    * Name ：     info_check
    * descript ： 检查注册信息是否有空值，以及两次密码是否相同
    * return ：   true 信息正常 false 信息异常
   ***************************************************************************/
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private boolean info_check(){
        if(account.getText().toString().isEmpty() ||
                passwd1.getText().toString().isEmpty() ||
                phone.getText().toString().isEmpty()){
            Looper.prepare();
            Toast.makeText(UserRegister.this,info.ACCOUNT_IS_EMMPUT,Toast.LENGTH_SHORT).show();
            Looper.loop();
            return true;
        }
        return false;
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
}
