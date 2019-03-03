package com.amap.map3d.demo.basic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.bumptech.glide.load.engine.Resource;
import com.example.yang.myapplication.R;
import com.example.yang.network.OkHttpManager;
import com.example.yang.util.CheckPermission;
import com.example.yang.util.UrlListdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
public class MapLocationPosition extends AppCompatActivity implements View.OnClickListener,AMapLocationListener{
    private final String MTAG = "MapLocationPosition";
    private AMap aMap;
    public String MAPRESOURCE = "mapresource";
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    //声明mListener对象，定位监听器
    private LocationSource.OnLocationChangedListener mListener = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    private MyLocationStyle myLocationStyle;


    //截图保存的名称
    private String screeshotname = null;
    private ImageButton returnimageButton;
    private Button sendbutton;
    private TheadAMapLocation theadAMapLocation = new TheadAMapLocation();


    //服务器返回消息
    public final int LOCALFRIENDS = 1;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int w = msg.what;
            switch (w) {
                case LOCALFRIENDS:    //case里不能用变量
                    DrawMarker();
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


        setContentView(R.layout.map_postion);
        CheckPermission.isGPSpermission(MapLocationPosition.this);
        if (CheckPermission.isGPSOpen(this) == true){
            CheckPermission.openGPS(this);
        }

        MapView mapView = (MapView) findViewById(R.id.map);//找到地图控件
        initActivity();

        Intent intent = getIntent();
        if(intent.getExtras().getString(MAPRESOURCE).equals("linkman_fragment"))
        {
            returnimageButton.setVisibility(View.GONE);
            sendbutton.setVisibility(View.GONE);
        }

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();//初始化地图控制器对象

        //适配marker的信息框
        LocalInfoWindowAdapter localInfoWindowAdapter = new LocalInfoWindowAdapter(aMap);
        aMap.setInfoWindowAdapter(localInfoWindowAdapter);//AMap类中

        initLocation();
        bluedot();
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

    /**************************************************************************
     * Name ：      bluedot
     * descript ：  初始化蓝点
     * return ：    void
    ***************************************************************************/
    public void bluedot()
    {
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0x44, 0, 0, 0xff));
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(10);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(0x22, 0, 0, 0xff));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        //设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。R.drawable.a用户头像
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.album);
        Bitmap bitzoom = zoomBitmap(bitmap,100,100);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(bitzoom));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
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
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(true);// 设置指南针是否显示
        uiSettings.setZoomControlsEnabled(false);// 设置缩放按钮是否显示
        uiSettings.setScaleControlsEnabled(true);// 设置比例尺是否显示
        uiSettings.setRotateGesturesEnabled(true);// 设置地图旋转是否可用
        uiSettings.setTiltGesturesEnabled(false);// 设置地图倾斜是否可用
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(false);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d(MTAG,"onLocationChanged");
        //http://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html?3D/com/amap/api/maps/model/class-use/LatLng.html
        //将屏幕坐标转换成地理坐标
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                /*//获取定位信息
                StringBuffer buffer = new StringBuffer();
                buffer.append(aMapLocation.getCountry() + ""
                        + aMapLocation.getProvince() + ""
                        + aMapLocation.getCity() + ""
                        + aMapLocation.getProvince() + ""
                        + aMapLocation.getDistrict() + ""
                        + aMapLocation.getStreet() + ""
                        + aMapLocation.getStreetNum());*/
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", "00000000001");//用户id
                map.put("Latitude", aMapLocation.getLatitude());
                map.put("Longitude", aMapLocation.getLongitude());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       ArrayList<MapNearbyInfo> mapinfoarrayList = theadAMapLocation.HTTPAMapRequst(map);
                        Message msg = new Message();
                        msg.obj = mapinfoarrayList;
                        msg.what = LOCALFRIENDS;
                        handler.sendMessage(msg);
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                //定位成功
                System.out.println("ResolveAMapLocation:succeed");
                if (isFirstLoc) {
                    System.out.println("ResolveAMapLocation:fail");
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
                    isFirstLoc = false;
                    //Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                }

                //添加图钉
                //aMap.addMarker(getMarkerOptions(amapLocation));

            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
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
                getMapScreenShot();
                Intent postionintent = new Intent();
                if (screeshotname != null) {
                    postionintent.putExtra("data",screeshotname);
                }
                setResult(1,postionintent);
                finish();
                break;
                default:
                    System.out.println("MapLocationPosition:onClick");
        }
    }

    /**************************************************************************
     * Name ：     getMapScreenShot
     * descript ： 对地图截图
     * return ：
    ***************************************************************************/
    private void getMapScreenShot()
    {
        /**
         * 对地图进行截屏
         */
        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {

            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int status) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                if(null == bitmap){
                    return;
                }
                try {
                    screeshotname = Environment.getExternalStorageDirectory() + "/test_"
                            + sdf.format(new Date()) + ".png";
                    FileOutputStream fos = new FileOutputStream(screeshotname);
                    boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    try {
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StringBuffer buffer = new StringBuffer();
                    if (b)
                        buffer.append("截屏成功 ");
                    else {
                        buffer.append("截屏失败 ");
                    }
                    if (status != 0)
                        buffer.append("地图渲染完成，截屏无网格");
                    else {
                        buffer.append( "地图未渲染完成，截屏有网格");
                    }
                    //ToastUtil.show(getApplicationContext(), buffer.toString());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**************************************************************************
     * Name ：     DrawMarker
     * descript ： 根据MARKER信息进行初始化 并设置监听
     * return ：   void
    ***************************************************************************/
    public void DrawMarker()
    {
        double longitude = 0;
        double latitude = 0;
        System.out.println("MapLocationPosition: --DrawMarker-- ");
        LatLng latLng = new LatLng(latitude,longitude);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker"));
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title("name").snippet("西安市：34.341568, 108.940174");

        //设置Marker可拖动
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.d_guzhang)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                System.out.println("MapLocationPosition: --DrawMarker-- click");
                marker.showInfoWindow();
                return false;
            }
        };
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

}
