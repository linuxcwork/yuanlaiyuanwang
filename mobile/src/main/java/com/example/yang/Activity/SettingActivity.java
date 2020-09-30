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

import com.example.yang.myapplication.Login;
import com.example.yang.myapplication.R;
import com.example.yang.network.OkHttpManager;
import com.example.yang.util.UrlListdb;
import com.example.yang.util.XmppConnection;

import java.util.Map;

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
    private Handler mhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                    case RESPONSE:
                        String response = (String) msg.obj;
                        logoutRsp(response);
                        break;
                }
            return false;
        }
	});

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //布局文件 setting_main
        setContentView(R.layout.setting_main);
        //账号与安全
        RelativeLayout accountandsec = findViewById(R.id.setting_activity_account_and_secure_RelativeLayout);
        accountandsec.setOnClickListener(this);
        RelativeLayout language = findViewById(R.id.setting_activity_language_setting_RelativeLayout);
        language.setOnClickListener(this);

        //退出
        RelativeLayout relativeLayout = findViewById(R.id.exit_login);
        relativeLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_activity_account_and_secure_RelativeLayout:
                Intent account_sec = new Intent(this,AccountAndSecureActivity.class);
                startActivity(account_sec);
                break;
            case R.id.setting_activity_language_setting_RelativeLayout:
                Intent intent_lang = new Intent(this,AppLanguageSettingActivity.class);
                startActivity(intent_lang);
                break;
            case R.id.exit_login:
                XmppConnection xmppConnection = XmppConnection.getInstance();
                xmppConnection.disConnect();
                Intent exit_login = new Intent(this,Login.class);
                startActivity(exit_login);
                break;
            default:
                Log.e(TAG,"NO ID");

        }
    }

    public void logoutRsp(String response){
        int count = 0;
        System.out.println("dfdsfsfsdfdsf"+response);
        Map<String, Object> map = http.getJsonObject(response);
        if(map.get("count") != null){
            count = (int)map.get("count");
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
