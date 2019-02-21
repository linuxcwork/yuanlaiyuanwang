package com.example.yang.myapplication;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.fragment.MenuFragmentpageAdapt;
import com.example.yang.util.CheckPermission;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    ImageButton signal_image;
    TextView signal_textview;
    ImageView linkman_image;
    TextView linkman_textview;
    ImageView life_image;
    TextView life_textview;
    ImageView own_image;
    TextView own_textview;
    ViewPager fragment_viewpage;

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private MenuFragmentpageAdapt mAdapter;

    /*
    * 主界面 fragment*/
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    public static final int PAGE_FOUR = 3;

    /*
    * 目录名*/
    String MAIN_FOLDER = "YLYW";
    String DATA = "data";
    Network network;

    public Handler mnHandler = new Handler(){
      public void HandleMassage(Message msg){
          switch (msg.what){
              case 1:
                  default:
                      break;
          }
          super.handleMessage(msg);
      }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        mAdapter = new MenuFragmentpageAdapt(manager);

        CheckPermission.isStorage(this);
        /*
            * 这是网络线程*/
       /*final BroadcastReceiver connectionRecever = new BroadcastReceiver(){

           @Override
           public void onReceive(Context context, Intent intent) {
               ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
               final NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
               NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
               if(!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()){
                   *//*
                   * 网络未连接*//*
                   Log.i(null,"网络未连接");
               }else{

                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           network = new Network();

                           Message net_message = new Message();
                           mnHandler.sendMessage(net_message);
                       }
                   }).start();

               }
           }
       };*/

        /*
        * ui初始化*/
        init_active_id();
        CreateFolder();

        //Intent mainac = getIntent();

        signal_image.setOnClickListener(this);
        signal_textview.setOnClickListener(this);
        linkman_image.setOnClickListener(this);
        linkman_textview.setOnClickListener(this);
        life_image.setOnClickListener(this);
        life_textview.setOnClickListener(this);
        own_image.setOnClickListener(this);
        own_textview.setOnClickListener(this);


    }

    public int init_active_id(){

        fragment_viewpage = (ViewPager) findViewById(R.id.fragment_main);
        signal_image = (ImageButton) findViewById(R.id.signal_main_image);
        signal_textview = (TextView) findViewById(R.id.signal_main_text);
        linkman_image = (ImageView) findViewById(R.id.linkman_main_image);
        linkman_textview = (TextView) findViewById(R.id.linkman_main_text);
        life_image = (ImageView) findViewById(R.id.life_main_image);
        life_textview = (TextView) findViewById(R.id.life_main_text);
        own_image = (ImageView) findViewById(R.id.own_main_image);
        own_textview = (TextView) findViewById(R.id.own_main_text);

        fragment_viewpage.setAdapter(mAdapter);
        fragment_viewpage.setCurrentItem(0);
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
            String MainFolder = Environment.getExternalStorageDirectory().getPath()+File.separator+MAIN_FOLDER;
            File dirFirstFile = new File(MainFolder);

            if(!dirFirstFile.exists()){
                dirFirstFile.mkdirs();
            }

            /*创建二级目录*/
            String DataFolder = MainFolder+File.separator+DATA;
            File DirSecendFile = new File(DataFolder);
            if(!DirSecendFile.exists()){
                DirSecendFile.mkdirs();
            }
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
            case R.id.signal_main_image:
            case R.id.signal_main_text:
                fragment_viewpage.setCurrentItem(PAGE_ONE);
                break;
            case R.id.linkman_main_image:
            case R.id.linkman_main_text:
                fragment_viewpage.setCurrentItem(PAGE_TWO);
                break;
            case R.id.life_main_image:
            case R.id.life_main_text:
                fragment_viewpage.setCurrentItem(PAGE_THREE);
                break;
            case R.id.own_main_image:
            case R.id.own_main_text:
                fragment_viewpage.setCurrentItem(PAGE_FOUR);
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

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (fragment_viewpage.getCurrentItem()) {
                case PAGE_ONE:
                    signal_image.setSelected(true);
                    signal_textview.setSelected(true);
                    break;
                case PAGE_TWO:
                    linkman_image.setSelected(true);
                    linkman_textview.setSelected(true);
                    break;
                case PAGE_THREE:
                    life_textview.setSelected(true);
                    life_image.setSelected(true);
                    break;
                case PAGE_FOUR:
                    own_image.setSelected(true);
                    own_textview.setSelected(true);
                    break;
            }
        }
    }
}
