package com.example.yang.Activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.example.yang.myapplication.R;
import com.example.yang.util.Bluetoothutil;
import com.suke.widget.SwitchButton;
import com.today.step.lib.ISportStepInterface;
import com.today.step.lib.TodayStepManager;
import com.today.step.lib.TodayStepService;

public class BluetoothSettingActivity extends AppCompatActivity {

    private ISportStepInterface iSportStepInterface;
    private Handler mDelayHandler = new Handler(new TodayStepCounterCall());
    private int mStepSum;
    private Bluetoothutil bluetoothutil;
    private Intent intent;

    private static final int REFRESH_STEP_WHAT = 0;
    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 3000;
    private boolean onbluetooth = true;
    private boolean isselected = false;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_bluetooth_nearby_xml);
        com.suke.widget.SwitchButton  recommend = findViewById(R.id.start_recommend_SwitchCompat);
        recommend.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job
                if (isChecked){
                    isselected = true;
                    startrecommend();
                    intent = new Intent(BluetoothSettingActivity.this, TodayStepService.class);
                    //初始化计步模块
                    TodayStepManager.startTodayStepService(getApplication());
                    //开启计步Service，同时绑定Activity进行aidl通信
                    startService(intent);
                    serviceConnection = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            //Activity和Service通过aidl进行通信
                            iSportStepInterface = ISportStepInterface.Stub.asInterface(service);
                            try {
                                mStepSum = iSportStepInterface.getCurrentTimeSportStep();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {

                        }
                    };
                    getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                } else {
                    isselected = false;
                    stoprecommend();
                    getApplicationContext().unbindService(serviceConnection);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startrecommend(){
        bluetoothutil = new Bluetoothutil(getApplicationContext());
        bluetoothutil.startBluetoothScan();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stoprecommend(){
        stopService(intent);
        bluetoothutil.stopBluetoothScan();
    }

    class TodayStepCounterCall implements Handler.Callback {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_STEP_WHAT: {
                    //每隔500毫秒获取一次计步数据刷新UI
                    if (null != iSportStepInterface) {
                        int step = 0;
                        try {
                            step = iSportStepInterface.getCurrentTimeSportStep();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (mStepSum != step) {
                            mStepSum = step;
                            isPause = true;
                            if(!onbluetooth && isselected){
                                startrecommend();
                                onbluetooth = true;
                            }
                        }else {
                            //计时器
                            if(isPause) {
                                isPause = false;
                                mhandmhandlele.postDelayed(timeRunable, currentSecond);
                            }
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

                    break;
                }
            }
            return false;
        }
    }

    /*****************计时器*******************/
    private Runnable timeRunable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            if (!isPause) {
                onbluetooth = false;
                stoprecommend();
            }
        }
    };
    //计时器
    private Handler mhandmhandlele = new Handler();
    private boolean isPause = false;//是否暂停
    private long currentSecond = 120000;//当前毫秒数
    /*****************计时器*******************/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
