package com.example.yang.myapplication;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.Activity.RecommendFriendActivity;
import com.example.yang.fragment.MenuFragmentpageAdapt;
import com.example.yang.util.Bluetoothutil;
import com.example.yang.util.CheckPermission;
import com.example.yang.util.FileOperationUtil;
import com.example.yang.util.XmppConnection;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.File;
import java.util.Objects;

import q.rorbin.badgeview.QBadgeView;

import static com.example.yang.myapplication.sqlite_linkmanmss.EKY_MESSAGETYPE;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_ACTNB;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_APPELLATION;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_CONTENT;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_CREDIT_VALUES;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_EMAIL;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_ISNEWMESSAGE;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_NAME;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_RELATION;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_ROWID;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_TIME;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private final String TAG = "MainActivity";
    private ImageView signal_image;
    private TextView signal_textview;
    private ImageView linkman_image;
    private TextView linkman_textview;
    /*ImageView life_image;
    TextView life_textview;*/
    private ImageView own_image;
    private TextView own_textview;
    private ViewPager fragment_viewpage;

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private MenuFragmentpageAdapt mAdapter;
    public static sqlite_linkmanmss sql;
    private MsgUpadteReceiver msgUpadteReceiver;
    private static int count = 0;

    public static String friendinfotable = "friendinfo";

    private final String CREATE_MESSAGE_DATABASE =
            "create table if not exists friendinfo( _id integer primary key autoincrement, " +
                    KEY_ROWID+" text, " +
                    KEY_NAME+" text," +
					KEY_APPELLATION+" text," +
                    KEY_ACTNB+" text," +
                    KEY_EMAIL+" text," +
                    KEY_RELATION+" INTEGER," +
                    EKY_MESSAGETYPE+" text," +
                    KEY_CREDIT_VALUES+" INTEGER,"+
                    KEY_ISNEWMESSAGE+" INTEGER," +
                    KEY_CONTENT+" text," +
                    KEY_TIME+" text);";

    private final String CREATE_BLESEARCH_DATABASE =
            "create table if not exists blescanresult( _id integer primary key autoincrement, " +
                    KEY_ROWID+" text, " +
                    KEY_NAME+" text," +
                    KEY_ACTNB+" text," +
                    KEY_TIME+" text,"+
                    "sex INTEGER," +
                    "status INTEGER," +
                    "age INTEGER," +
                    "credit_value text," +
                    "real_name INTEGER,"+
                    "independent_money INTEGER," +
                    "position text);";

    /*
    * 主界面 fragment*/
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    public static final int PAGE_FOUR = 3;

    /*
    * 目录名*/
    String DATA = "data";

    private Handler mnHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.layout);
        sql = new sqlite_linkmanmss(this, "link", null, 1);
        sql.open();
        sql.CreateTable(CREATE_MESSAGE_DATABASE);
        sql.CreateTable(CREATE_BLESEARCH_DATABASE);
        sql.close();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        mAdapter = new MenuFragmentpageAdapt(manager);

        Intent intent = getIntent();
        int page_fragment = intent.getIntExtra("num",1);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.yang.myapplication.chat_contrue.updaterecv");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        msgUpadteReceiver = new MsgUpadteReceiver();
        //注册广播
        registerReceiver(msgUpadteReceiver,intentFilter);
        /*
         * ui初始化*/
        init_active_id(page_fragment);
        CreateFolder();
    }


    private class MsgUpadteReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Objects.equals(intent.getAction(),"com.example.yang.updaterecv")) {
               /* sql.open();
                Cursor cursor = sql.getContact(MainActivity.friendinfotable,sql.KEY_ACTNB,intent.getStringExtra("account"));
                if(cursor != null && cursor.moveToFirst()){*/
                    count++;// = Integer.parseInt(cursor.getString(cursor.getColumnIndex(sql.KEY_ISNEWMESSAGE)));
              /*  }
                sql.close();*/
                new QBadgeView(MainActivity.this).bindTarget(signal_image)
                        .setBadgeGravity(Gravity.END | Gravity.TOP)
                        .setBadgeNumber(count);
            }else if(Objects.equals(intent.getAction(),"android.net.conn.CONNECTIVITY_CHANGE")){
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    Toast.makeText(context, "当前网络可用", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public int init_active_id(int num) {

        LinearLayout messagelin = findViewById(R.id.main_activity_menu_message);
        messagelin.setOnClickListener(this);
        LinearLayout linkmanlin = findViewById(R.id.main_activity_menu_linkman);
        linkmanlin.setOnClickListener(this);
        LinearLayout myselflin = findViewById(R.id.main_activity_menu_myself);
        myselflin.setOnClickListener(this);
        fragment_viewpage =  findViewById(R.id.fragment_main);
        signal_image =  findViewById(R.id.signal_main_image);
    /*    sqlite_linkmanmss sql = new sqlite_linkmanmss(this,null,null,null);
        sql.open();
        sql.CreateTable(null);
        Cursor cursor = sql.getContact(sql.KEY_ISNEWMESSAGE,"true");*/
        //显示消息数
      /*  new QBadgeView(this).bindTarget(signal_image)
                .setBadgeGravity(Gravity.END | Gravity.TOP)
                .setBadgeNumber(cursor.getCount());*/
        signal_textview =  findViewById(R.id.signal_main_text);
        linkman_image =  findViewById(R.id.linkman_main_image);
        linkman_textview =  findViewById(R.id.linkman_main_text);
        /*life_image =  findViewById(R.id.life_main_image);
        life_textview =  findViewById(R.id.life_main_text);*/
        own_image =  findViewById(R.id.own_main_image);
        own_textview =  findViewById(R.id.own_main_text);
        ImageView searchimage = findViewById(R.id.tianjia_main);
        searchimage.setOnClickListener(this);
        /*life_image.setOnClickListener(this);
        life_textview.setOnClickListener(this);*/

        fragment_viewpage.setAdapter(mAdapter);
        if(num >=0 && num < 4){
            fragment_viewpage.setCurrentItem(num);
        }else{
            fragment_viewpage.setCurrentItem(1);
        }
        fragment_viewpage.addOnPageChangeListener(this);

        return 0;
    }

    public void CreateFolder(){
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if(!sdCardExist)
        {
            Toast.makeText(this,"请插入外部SD存储卡",Toast.LENGTH_LONG);

        }else {
            /*
            * 创建一级目录*/
            String MainFolder = Environment.getExternalStorageDirectory().getPath()+File.separator+FileOperationUtil.MAIN_FOLDER;
            FileOperationUtil.CreateDir(MainFolder);

            /*创建二级目录*/
            String DataFolder = MainFolder+File.separator+DATA;
            FileOperationUtil.CreateDir(DataFolder);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.main_activity_menu_message:
                fragment_viewpage.setCurrentItem(PAGE_ONE);
                break;
            case R.id.main_activity_menu_linkman:
                fragment_viewpage.setCurrentItem(PAGE_TWO);
                break;
            case R.id.life_main_image:
            case R.id.life_main_text:
                fragment_viewpage.setCurrentItem(PAGE_THREE);
                break;
            case R.id.main_activity_menu_myself:
                fragment_viewpage.setCurrentItem(PAGE_FOUR);
                break;
            case R.id.tianjia_main:

                break;

                default:
        }
    }

/*
* fragment viewpage*/
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (fragment_viewpage.getCurrentItem()) {
                case PAGE_ONE:
                    signal_image.setSelected(true);
                    signal_textview.setTextColor(R.color.green);
                    break;
                case PAGE_TWO:
                    linkman_image.setSelected(true);
                    linkman_textview.setTextColor(R.color.green);
                    break;
               /* case PAGE_THREE:
                    life_image.setSelected(true);
                    life_textview.setTextColor(R.color.green);
                    break;*/
                case PAGE_FOUR:
                    own_image.setSelected(true);
                    own_textview.setTextColor(R.color.green);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgUpadteReceiver);
    }
}
