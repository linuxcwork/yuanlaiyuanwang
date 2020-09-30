package com.example.yang.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.map3d.demo.basic.LocationService;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.example.yang.adapter.AnnounceAdapt;
import com.example.yang.myapplication.R;
import com.example.yang.network.CheckNetwork;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnounceActivity extends AppCompatActivity {

    public TextView positiontext;
    private  final int POSITIONCHANGE = 1;
    private List<Map<String, Object>> listall=new ArrayList<Map<String,Object>>();
    private MyLocationListener myListener = new MyLocationListener();
    private CityHolder cityHolder = new CityHolder();

    //开始定位，这里模拟一下定位
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case POSITIONCHANGE:
                    positiontext.setText(msg.obj.toString());
                    RefreshAnnounce();
                    break;
            }
            return false;
        }
        });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_announce_activity);
        ListView announce = findViewById(R.id.contact_announce_listview);
        LinearLayout position = findViewById(R.id.anounnce_activity_position);
        if(CheckNetwork.isNetworkConnected(this) ||  CheckNetwork.isWifiConnected(this)) {
            LocationService locationService = new LocationService(getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setIsNeedAddress(true);
            locationService.setLocationOption(option);
            locationService.registerListener(myListener);
            locationService.start();
        }
        positiontext = findViewById(R.id.anounnce_activity_position_text);
        FloatingActionButton addannounce = findViewById(R.id.announce_activity_add);
        AppCompatSpinner time_apinner = findViewById(R.id.anounnce_activity_spinner_time);
        List<Map<String, Object>> list = getData();
        announce.setAdapter(new AnnounceAdapt(this, list));
        announce.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detial = new Intent(AnnounceActivity.this,AnnounceDetialedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type",listall.get(position).get("type").toString());
                bundle.putString("title",listall.get(position).get("title").toString());
                bundle.putString("image",listall.get(position).get("image").toString());
                bundle.putString("sponsor",listall.get(position).get("sponsor").toString());
                bundle.putString("startdate",listall.get(position).get("startdate").toString());
                bundle.putString("enddate",listall.get(position).get("enddate").toString());
                bundle.putString("starttime",listall.get(position).get("starttime").toString());
                bundle.putString("endtime",listall.get(position).get("endtime").toString());
                bundle.putString("addr",listall.get(position).get("addr").toString());
                bundle.putString("tel",listall.get(position).get("tel").toString());
                bundle.putString("num",listall.get(position).get("num").toString());
                bundle.putString("note",listall.get(position).get("note").toString());
                detial.putExtra("detial",bundle);
                startActivity(detial);
            }
        });

        position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddress();
            }
        });
        time_apinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityHolder.time = position;
                RefreshAnnounce();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addannounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(AnnounceActivity.this,AnnounceAddActivity.class);
                startActivity(add);
            }
        });
    }

    private List<Map<String, Object>> getData(){
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("type","type");
            map.put("title","中华人民共和国");
            map.put("image", R.drawable.ic_launcher_background);
            map.put("sponsor", "主办方"+i);
            map.put("startdate", "开始日期" + i);
            map.put("enddate", "结束日期" + i);
            map.put("endtime", "开始时间" + i);
            map.put("starttime", "结束时间" + i);
            map.put("addr", "地址" + i);
            map.put("tel", "电话" + i);
            map.put("num", "人数" + i);
            map.put("note", "注意事项" + i);
            listall.add(map);
        }

        return listall;
    }

    private void getAddress()   {
        List<HotCity> hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));

       CityPicker.getInstance()
                .setFragmentManager(getSupportFragmentManager())  //此方法必须调用
                .enableAnimation(true)  //启用动画效果
               // .setAnimationStyle()  //自定义动画
                .setLocatedCity(new LocatedCity(cityHolder.city, cityHolder.province, cityHolder.code))
                .setHotCities(hotCities)  //指定热门城市
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        Message message = new Message();
                        message.obj = data.getName();
                        message.what = POSITIONCHANGE;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onLocate() {

                    }
                })
                .show();
    }

    /*******************************************************************
    * 获取位置信息
     * *****************************************************************/
    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            System.out.println(location.getCity());
            if(location.getCity() != null) {
                cityHolder.province = location.getProvince();     //获取省份
                cityHolder.city = location.getCity();    //获取城市
                cityHolder.code = location.getAdCode();  //获取code
                cityHolder.time = 3;

                Message message = new Message();
                message.obj = location.getCity();
                message.what = POSITIONCHANGE;
                handler.sendMessage(message);
            }
        }
    }

    /***************************************************************
     * 获取配置信息 刷新公告数据
     * *************************************************************/
    public void RefreshAnnounce(){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    class CityHolder {
        public String province;
        public String city;
        public String code;
        public int time;
    }

}
