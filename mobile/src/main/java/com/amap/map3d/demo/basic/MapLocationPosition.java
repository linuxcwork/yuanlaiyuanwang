package com.amap.map3d.demo.basic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.yang.myapplication.R;
import com.example.yang.util.CheckPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.Activity
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/11/25 12:34
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class MapLocationPosition extends AppCompatActivity implements View.OnClickListener {
    private final String MTAG = "MapLocationPosition";
    public String MAPRESOURCE = "mapresource";
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    private LocationClient locationClient;

    //截图保存的名称
    private String screeshotname = null;
    private ImageButton returnimageButton;
    private Button sendbutton;
    private TheadAMapLocation theadAMapLocation = new TheadAMapLocation();
    private MyLocationListener myLocationListener;
    private MapView mapView;
    private BaiduMap mBaiduMap;
    //服务器返回消息
    public final int LOCALFRIENDS = 1;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int w = msg.what;
            switch (w) {
                case LOCALFRIENDS:    //case里不能用变量

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MapLocationPosition: onCreate");

        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.map_postion);
        CheckPermission.isGPSpermission(MapLocationPosition.this);
        if (CheckPermission.isGPSOpen(this) == true){
            CheckPermission.openGPS(this);
        }

        mapView = (MapView) findViewById(R.id.map);//找到地图控件
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        initActivity();

        Intent intent = getIntent();
        if(intent.getExtras().getString(MAPRESOURCE).equals("linkman_fragment"))
        {
            returnimageButton.setVisibility(View.GONE);
            sendbutton.setVisibility(View.GONE);
        }

        initLocation();
    }

    /**************************************************************************
     * Name ：     initActivity
     * descript ： 初始化界面控件
     * return ：
    ***************************************************************************/
    public void initActivity()
    {
        returnimageButton = findViewById(R.id.map_return);
        returnimageButton.setOnClickListener(this);
        sendbutton = findViewById(R.id.map_send);
        sendbutton.setOnClickListener(this);
    }

    public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float)w / width);
        float scaleHeight = ((float)h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbmp;
    }

    /**************************************************************************
     * Name ：     initLocation
     * descript ： 被MapLoactionPosition 调用
     *              初始化定位功能
     * return ：   无返回值
     ***************************************************************************/
    public void initLocation()
    {
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        locationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        myLocationListener = new MyLocationListener();
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.start();
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            System.out.println("dddddddddddddddddddddddddddddd");
            //mapView 销毁后不在处理新接收的位置
            if (bdLocation == null || mapView == null){
                return;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(bdLocation.getRadius()).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {
                LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", "00000000001");//用户id
            map.put("latitude", bdLocation.getLatitude());
            map.put("longitude", bdLocation.getLongitude());
            map.put("requestcode","getposition");
            Toast.makeText(MapLocationPosition.this,bdLocation.getAddrStr(),Toast.LENGTH_SHORT);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<MapNearbyInfo> mapinfoarrayList = theadAMapLocation.HTTPAMapRequst(map);
                    Message msg = new Message();
                    msg.obj = mapinfoarrayList;
                    msg.what = LOCALFRIENDS;
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }





    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.map_return:
                Intent goback = new Intent();
                setResult(1);
                finish();
                break;
            case R.id.map_send:

                break;
                default:
                    System.out.println("MapLocationPosition:onClick");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.unRegisterLocationListener(myLocationListener);
        locationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理

    }

    class MapNearbyInfo{
        //头像
        private File nearbyicon;
        //是否结婚
        private String ismarry;
        //姓名
        private String name;
        //情感状况
        private short emotion;
        //是否实名认证
        private Boolean isreal;
        //信用值
        private int reliablecount;
        //诚意金
        private Boolean ispay;
        //性别
        private String sex;
        //年龄
        private int age;
        //纬度
        private double mapLatitude;
        //经度
        private double mapLongitude;


        public File getNearbyicon() {
            return nearbyicon;
        }

        public void setNearbyicon(File nearbyicon) {
            this.nearbyicon = nearbyicon;
        }

        public String getIsmarry() {
            return ismarry;
        }

        public void setIsmarry(String ismarry) {
            this.ismarry = ismarry;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public double getMapLatitude() {
            return mapLatitude;
        }

        public void setMapLatitude(double mapLatitude) {
            this.mapLatitude = mapLatitude;
        }

        public double getMapLongitude() {
            return mapLongitude;
        }

        public void setMapLongitude(double mapLongitude) {
            this.mapLongitude = mapLongitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public short getEmotion() {
            return emotion;
        }

        public void setEmotion(short emotion) {
            this.emotion = emotion;
        }

        public Boolean getIsreal() {
            return isreal;
        }

        public void setIsreal(Boolean isreal) {
            this.isreal = isreal;
        }

        public int getReliablecount() {
            return reliablecount;
        }

        public void setReliablecount(int reliablecount) {
            this.reliablecount = reliablecount;
        }

        public Boolean getIspay() {
            return ispay;
        }

        public void setIspay(Boolean ispay) {
            this.ispay = ispay;
        }
    }

}
