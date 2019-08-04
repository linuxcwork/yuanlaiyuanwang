package com.example.yang.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.network.OkHttpManager;
import com.example.yang.network.ServerResponse;
import com.example.yang.util.FileOperationUtil;
import com.example.yang.util.SystemUtil;
import com.example.yang.util.UrlListdb;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


public class Login extends Activity implements View.OnClickListener{

    private EditText admin;
    private EditText passwd;
    private TextView byshortmassage;
    private Button login;
    private TextView forgetpasswd;
    private TextView regist;
    private final int RESPONSE = 1;

    //目录名
    private String MainFolder = null;
    private ServerResponse serverResponse = new ServerResponse();
    private UrlListdb urlListdb = new UrlListdb();
    private OkHttpManager http = new OkHttpManager();

    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESPONSE:
                    Map<String, Object> response =(Map<String, Object>) msg.obj;
                    LoginResponse(response);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        /*
         * 创建一级目录*/
        MainFolder = FileOperationUtil.getMainDir();
        FileOperationUtil.CreateDir(MainFolder);

        admin  = (EditText) findViewById(R.id.login_administrator);
        passwd = (EditText) findViewById(R.id.login_passwd);
        byshortmassage = (TextView) findViewById(R.id.login_bymessage);
        login  = (Button) findViewById(R.id.login_main_login);
        forgetpasswd = (TextView) findViewById(R.id.login_forget_passwd_textview);
        regist = (TextView) findViewById(R.id.login_register_textview);

        byshortmassage.setOnClickListener(this);
        login.setOnClickListener(this);
        forgetpasswd.setOnClickListener(this);
        regist.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_bymessage:
                Intent phonelogin = new Intent(Login.this, PhoneLogin.class);
                startActivity(phonelogin);
                break;
            case R.id.login_main_login:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!admin.getText().equals("") && !passwd.getText().equals("")) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("id", admin.getText());
                            map.put("passwd", passwd.getText());
                            map.put("devicename",SystemUtil.getSystemModel());

                            http.postKeyValuePaires(urlListdb.login, map, new HttpResponse() {
                                @Override
                                public void succesd(Call call,Map<String, Object> response) {
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
                        } else {
                            if (admin.getText().equals("")) {
                                print("请输入用户名");
                            } else if (passwd.getText().equals("")) {
                                print("请输入密码");
                            } else {
                                print("请输入用户名和密码");
                            }
                        }

                    }
                }).start();

                break;
            case R.id.login_forget_passwd_textview:
                Intent forgetpasswd = new Intent(Login.this, ForgetPasswd.class);
                startActivity(forgetpasswd);
                break;
            case R.id.login_register_textview:
                Intent register = new Intent(Login.this, UserRegister.class);
                startActivity(register);
                finish();
                break;
        }
    }

    private void LoginResponse(Map<String, Object> response){
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        /*if(response.get("loginstate") == null){
            print("用户名或密码错误，请重试");
            return ;
        }
        String loginstate = response.get("loginstate").toString();
        if (loginstate.equals(serverResponse.LOGINSUCCEED)) {
            UpdateFile(response);
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        } else if (loginstate.equals(serverResponse.LOGINFAIL)) {
            String statet = String.valueOf(response.get("state"));
            String lastlogintime = String.valueOf(response.get("logintime"));
            String devicename = String.valueOf(response.get("devicename"));
            if(statet != null && statet.equals("1") &&
                    lastlogintime != null &&
                    devicename != null){
                print(lastlogintime+" 已登陆,设备名"+devicename);
            }else {
                print("用户名或密码错误，请重试");
            }
        }*/
    }

    public void UpdateFile(Map<String, Object> map){

        /*创建二级目录用户个人信息*/
        String DataFolder = MainFolder+File.separator+admin.getText();
        FileOperationUtil.CreateDir(DataFolder);
        //用户个人信息存储文件
        String userconfig = DataFolder+File.separator+admin.getText();
        FileOperationUtil.CreateFile(userconfig);
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String key : map.keySet()) {
                editor.putString(key, (String) map.get(key));
        }
        editor.putString("account",admin.getText().toString());
        editor.putString("passwd",passwd.getText().toString());
        editor.putString("state","1");
        editor.commit();
        /*try {
            FileOutputStream input =new FileOutputStream(userconfig);
            input.write(("账号："+admin.getText()+"\r\n").getBytes());
            input.write(("密码："+passwd.getText()+"\r\n").getBytes());
            input.write(("状态: "+"1"+"\r\n").getBytes());
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void print(String massage){
        Toast toast = Toast.makeText(this,massage, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP , 0, 80);
        toast.show();
    }
}
