package com.example.yang.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amap.map3d.demo.basic.LocationService;
import com.amap.map3d.demo.basic.MapLBSUrl;
import com.amap.map3d.demo.basic.MapLocationPosition;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yang.myapplication.HttpResponse;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.sqlite_linkmanmss;
import com.example.yang.network.OkHttpManager;
import com.example.yang.util.SharedPreferencedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;

public class PositionSettingActivity extends AppCompatActivity implements View.OnClickListener {
    public final String TAG = "PositionSettingActivity";
    private LocationService locationService;
    private OkHttpManager http = new OkHttpManager();
    private Calendar calendar= Calendar.getInstance(Locale.CHINA);
    private TextView firsttimestart;
    private TextView firsttimetime;
    private TextView secendtimestart;
    private TextView secendtimeend;
    private TextView thirdtimestart;
    private TextView thirdtimeend;
    private ImageView first_zone;
    private ImageView second_zone;
    private ImageView third_zopne;

    private String account = null;

    private static String id = null;

    private Handler handler= new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String resp = (String) msg.obj;
                    try {
                        JSONObject json = new JSONObject(resp);
                        if(json != null){
                            String jid = json.getString("id");
                            id = jid;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.own_position_setting_activity);
        SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();
        account = sharedPreferencedUtils.getUserInfo(this, sqlite_linkmanmss.KEY_ACTNB,"null");
        intiView();
    }

    public void intiView(){
        //启动位置服务
        SwitchCompat serviceswitch = findViewById(R.id.own_fragment_location_service_SwitchCompat);
        serviceswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    initPostion();
                } else {
                    positionStop();
                }
            }
        });
        //安全模式
        SwitchCompat position = findViewById(R.id.user_safe);
        position.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    initPostion();
                } else {
                    positionStop();
                }
            }
        });
        SwitchCompat firsttime = findViewById(R.id.first_time_switch);
        firsttimestart = findViewById(R.id.position_setting_time_first_start);
        firsttimetime = findViewById(R.id.position_setting_time_first_end);
        firsttimetime.setClickable(false);
        firsttimestart.setClickable(false);
        firsttimestart.setOnClickListener(this);
        firsttimetime.setOnClickListener(this);
        firsttime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    firsttimestart.setTextColor(getResources().getColor(R.color.green));
                    firsttimestart.setClickable(true);
                    firsttimetime.setTextColor(getResources().getColor(R.color.green));
                    firsttimetime.setClickable(true);
                } else {
                    firsttimestart.setTextColor(getResources().getColor(R.color.gray));
                    firsttimestart.setClickable(false);
                    firsttimetime.setTextColor(getResources().getColor(R.color.gray));
                    firsttimetime.setClickable(false);
                }
            }
        });

        SwitchCompat sencendtime = findViewById(R.id.secend_time_switch);
        secendtimestart = findViewById(R.id.position_setting_time_secend_start);
        secendtimeend = findViewById(R.id.position_setting_time_secend_end);
        secendtimestart.setClickable(false);
        secendtimeend.setClickable(false);
        secendtimestart.setOnClickListener(this);
        secendtimeend.setOnClickListener(this);
        sencendtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    secendtimestart.setTextColor(getResources().getColor(R.color.green));
                    secendtimestart.setClickable(true);
                    secendtimeend.setTextColor(getResources().getColor(R.color.green));
                    secendtimeend.setClickable(true);
                } else {
                    secendtimestart.setTextColor(getResources().getColor(R.color.gray));
                    secendtimestart.setClickable(false);
                    secendtimeend.setTextColor(getResources().getColor(R.color.gray));
                    secendtimeend.setClickable(false);
                }
            }
        });

        SwitchCompat thirdtime = findViewById(R.id.secend_time_switch);
        thirdtimestart = findViewById(R.id.position_setting_time_third_start);
        thirdtimeend = findViewById(R.id.position_setting_time_third_end);
        thirdtimestart.setClickable(false);
        thirdtimeend.setClickable(false);
        thirdtimestart.setOnClickListener(this);
        thirdtimeend.setOnClickListener(this);
        thirdtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    thirdtimestart.setTextColor(getResources().getColor(R.color.green));
                    thirdtimestart.setClickable(true);
                    thirdtimeend.setTextColor(getResources().getColor(R.color.green));
                    thirdtimeend.setClickable(true);
                } else {
                    thirdtimestart.setTextColor(getResources().getColor(R.color.gray));
                    thirdtimestart.setClickable(false);
                    thirdtimeend.setTextColor(getResources().getColor(R.color.gray));
                    thirdtimeend.setClickable(false);
                }
            }
        });

        first_zone = findViewById(R.id.first_safe_zone);
        second_zone = findViewById(R.id.second_safe_zone);
        third_zopne = findViewById(R.id.third_safe_zone);
        first_zone.setOnClickListener(this);
        second_zone.setOnClickListener(this);
        third_zopne.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.position_setting_time_first_start:
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                        // 绑定监听器
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = hourOfDay + ":" + minute;
                                firsttimestart.setText(time);
                            }
                        }
                        // 设置初始时间
                        , calendar.get(Calendar.HOUR_OF_DAY)
                        , calendar.get(Calendar.MINUTE)
                        // true表示采用24小时制
                        , true).show();
                break;
            case R.id.position_setting_time_first_end:
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                        // 绑定监听器
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = hourOfDay + ":" + minute;
                                firsttimetime.setText(time);
                            }
                        }
                        // 设置初始时间
                        , calendar.get(Calendar.HOUR_OF_DAY)
                        , calendar.get(Calendar.MINUTE)
                        // true表示采用24小时制
                        , true).show();
                break;
            case R.id.position_setting_time_secend_start:
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                        // 绑定监听器
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = hourOfDay + ":" + minute;
                                secendtimestart.setText(time);
                            }
                        }
                        // 设置初始时间
                        , calendar.get(Calendar.HOUR_OF_DAY)
                        , calendar.get(Calendar.MINUTE)
                        // true表示采用24小时制
                        , true).show();
                break;
            case R.id.position_setting_time_secend_end:
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                        // 绑定监听器
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = hourOfDay + ":" + minute;
                                secendtimeend.setText(time);
                            }
                        }
                        // 设置初始时间
                        , calendar.get(Calendar.HOUR_OF_DAY)
                        , calendar.get(Calendar.MINUTE)
                        // true表示采用24小时制
                        , true).show();
                break;
            case R.id.position_setting_time_third_start:
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                        // 绑定监听器
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = hourOfDay + ":" + minute;
                                thirdtimestart.setText(time);
                            }
                        }
                        // 设置初始时间
                        , calendar.get(Calendar.HOUR_OF_DAY)
                        , calendar.get(Calendar.MINUTE)
                        // true表示采用24小时制
                        , true).show();
                break;
            case R.id.position_setting_time_third_end:
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                        // 绑定监听器
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = hourOfDay + ":" + minute;
                                thirdtimeend.setText(time);
                            }
                        }
                        // 设置初始时间
                        , calendar.get(Calendar.HOUR_OF_DAY)
                        , calendar.get(Calendar.MINUTE)
                        // true表示采用24小时制
                        , true).show();
                break;
            case R.id.first_safe_zone:
                Intent firsafemap = new Intent(this, MapLocationPosition.class);
                firsafemap.putExtra("mapresource","PositionSettingActivity");
                startActivityForResult(firsafemap,0);
                break;
            case R.id.second_safe_zone:
                Intent secsafemap = new Intent(this, MapLocationPosition.class);
                secsafemap.putExtra("mapresource","PositionSettingActivity");
                startActivityForResult(secsafemap,1);
                break;
            case R.id.third_safe_zone:
                Intent thisafemap = new Intent(this, MapLocationPosition.class);
                thisafemap.putExtra("mapresource","PositionSettingActivity");
                startActivityForResult(thisafemap,2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle pathshot = data.getExtras();
            String btext = pathshot.get("image").toString();
            if(requestCode == 0){
                Glide
                        .with(this)
                        .load(btext)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .centerCrop()
                        .skipMemoryCache(true) // 不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                        .into(first_zone);
            }else if(requestCode == 1){
                Glide
                        .with(this)
                        .load(btext)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .centerCrop()
                        .skipMemoryCache(true) // 不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                        .into(second_zone);
            }else if(requestCode == 2){
                Glide
                        .with(this)
                        .load(btext)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .centerCrop()
                        .skipMemoryCache(true) // 不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                        .into(third_zopne);
            }
        }
    }

    public void initPostion(){
        Log.d(TAG,"INIT POSTION");
        locationService = new LocationService(getApplicationContext());
        locationService.setDefaultLocationOption();
        locationService.registerListener(mListener);
        locationService.start();
    }

    public void positionStop(){
        Log.d(TAG,"STOP POSITION");
        if(locationService == null){
            return ;
        }
        locationService.unregisterListener(mListener);
        locationService.stop();
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // TODO Auto-generated method stub
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {

                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        .direction(bdLocation.getRadius()).latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude()).build();
                /**
                 *
                 * 1.服务器查询是否存在id
                 * 2.如果存在获取id，并更新百度LBS的位置信息和状态位
                 * 3.如果不存在，则创建一个新的数据并，将ID号同步到服务器，同时更新状态
                 *
                 * 4.当关闭位置共享是，将状态为设为false，并且不接受来自地图功能的信息*/
                Map<String, Object> map = new HashMap<String, Object>();
                if (isexitPosition()){
                    map.put("id",id);
                    map.put("title", account);//用户id
                    map.put("geotable_id", "201098");
                    map.put("address", bdLocation.getCity());
                    map.put("longitude", bdLocation.getLongitude());
                    map.put("latitude", bdLocation.getLatitude());
                    map.put("coord_type", 1);
                    map.put("ak", "pFsB5nTTUw5l4gUKYRGgViSX7tMx2IOR");
                    Send(MapLBSUrl.update_poi,map);
                } else {
                    map.put("title", account);//用户id
                    map.put("geotable_id", "201098");
                    map.put("address", bdLocation.getCity());
                    map.put("longitude", bdLocation.getLongitude());
                    map.put("latitude", bdLocation.getLatitude());
                    map.put("coord_type", 1);
                    map.put("ak", "pFsB5nTTUw5l4gUKYRGgViSX7tMx2IOR");
                    Send(MapLBSUrl.create_poi, map);
                }
            }
        }

    };


    private void Send(String url,Map<String, Object> map){

                http.postKeyValuePaires(url, map, new HttpResponse() {
                    @Override
                    public void succesd(Call call, String response) {
                        Message message = new Message();
                        message.obj = response;
                        message.what = 1;
                        handler.sendMessage(message);
                        System.out.println(response);
                    }

                    @Override
                    public void failed(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(PositionSettingActivity.this,"错误",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                });
    }

    private boolean isexitPosition(){
        boolean isexit = false;
        if(id != null){
            isexit = true;
        }
        return isexit;
    }
}
