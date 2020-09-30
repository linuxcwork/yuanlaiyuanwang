package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.http.HttpClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.tts.loopj.HttpGet;
import com.example.yang.Activity.PositionSettingActivity;
import com.example.yang.myapplication.HttpResponse;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.chat_contrue;
import com.example.yang.network.OkHttpManager;
import com.example.yang.util.CheckPermission;
import com.example.yang.util.XmppConnection;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;

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
public class MapLocationPosition extends AppCompatActivity implements View.OnClickListener,BaiduMap.SnapshotReadyCallback {
    private final String MTAG = "MapLocationPosition";
    private String MAPRESOURCE = "mapresource";
    private static String preActivity;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    private LocationClient locationClient;

    //截图保存的名称
    private String screeshotname = null;
    private ImageView returnimageButton;
    private Button sendbutton;
    private TheadAMapLocation theadAMapLocation = new TheadAMapLocation();
    private MyLocationListener myLocationListener;
    private MapView mapView;
    private static BaiduMap mBaiduMap;
    private Map<String, Object> map = new HashMap<String, Object>();
    private List<LatLng> points = new ArrayList<LatLng>();
    private Bitmap mapsnapshot;
    private OkHttpManager okHttpManager;

    private String urlposition = "http://api.map.baidu.com/geosearch/v3/nearby?ak=9MhkkWQvZ4eNmXquH8F9rYObXr54lnfS" +
            "&geotable_id=201098" +
            "&radius=1000"+
            "&mcode=BB:6B:F0:7A:3A:B3:8A:5B:D8:B9:93:1C:03:D8:24:F8:60:80:3F:A4;com.example.yang.myapplication";

    private List<Map<String, Object>> nerposlist = new ArrayList<>();
    //服务器返回消息
    private final int LOCALFRIENDS = 1;
    private final int MAPSENDPOSITION = 2;
    Handler handler = new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOCALFRIENDS:
                    if(okHttpManager != null) {
                        nerposlist.clear();
                        nerposlist = okHttpManager.getJsonArray(msg.obj.toString());

                        //创建OverlayOptions的集合
                        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_room_black_24dp);
                        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_room_black_24dp);
                        for(int i = 0;i<nerposlist.size();i++) {
                            String sex = (String) nerposlist.get(i).get("sex");
                            //构造大量坐标数据
                            LatLng point = new LatLng((double)nerposlist.get(i).get("latitude"),(double)nerposlist.get(i).get("longitude"));

                           /* BitmapDescriptor bitmap;
                            if(sex == null){
                                continue;
                            }else if(sex.equals("0")){
                                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_room_black_24dp);
                            }else {
                                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_mic_off_black_24dp);
                            }*/
                            //创建OverlayOptions属性
                            OverlayOptions option = new MarkerOptions()
                                    .title(nerposlist.get(i).get("title").toString())
                                    .position(point)
                                    .position(point)
                                    .icon(bitmap);
                            options.add(option);
                        }
                        //在地图上批量添加
                        mBaiduMap.addOverlays(options);
                    }
                    break;
                case  MAPSENDPOSITION:
                    Bundle bundle = new Bundle();
                    bundle.putString("image", Base64.encodeToString(flattenBitmap((Bitmap) msg.obj),0));
                    Intent sendmap;
                    if(preActivity != null && preActivity.equals("chat_contrue")) {
                         sendmap = new Intent(MapLocationPosition.this, chat_contrue.class);
                    }else {
                         sendmap = new Intent(MapLocationPosition.this, PositionSettingActivity.class);
                    }
                    sendmap.putExtras(bundle);
                    MapLocationPosition.this.setResult(Activity.RESULT_OK,sendmap);
                    MapLocationPosition.this.finish();
                break;
                default:
                    break;
            }
            return false;
        }
        });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MapLocationPosition: onCreate");

        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.map_postion);
        CheckPermission.isGPSpermission(MapLocationPosition.this);
        if (CheckPermission.isGPSOpen(this)){
            CheckPermission.openGPS(this);
        }

        mapView = findViewById(R.id.map);//找到地图控件
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMarkerClickListener(marklistener);
        initActivity();

        okHttpManager = new OkHttpManager();

        Intent intent = getIntent();
        preActivity = intent.getStringExtra(MAPRESOURCE);
        if(preActivity != null && Objects.requireNonNull(preActivity.equals("linkman_fragment")))
        {
            returnimageButton.setVisibility(View.GONE);
            sendbutton.setVisibility(View.GONE);
        }else if(Objects.requireNonNull(preActivity.equals("PositionSettingActivity"))){
            System.out.println("PositionSettingActivity");
            //设置地图单击事件监听
            mBaiduMap.setOnMapClickListener(singlelistener);
            //设置触摸地图事件监听者
            mBaiduMap.setOnMapTouchListener(listener);
        }else if(Objects.requireNonNull(preActivity.equals("chat_contrue"))){
            System.out.println("chat_contrue");
        }

        initLocation();
    }

    private static byte[] flattenBitmap(Bitmap bitmap) {
        int size = bitmap.getWidth() * bitmap.getHeight() * 4; //每个像素占32bit，所以*4
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            Log.w("Favorite", "Could not write icon");
            return null;

        }
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
        sendbutton = findViewById(R.id.editreport);
        sendbutton.setOnClickListener(this);
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

            String url = urlposition+"&location="+bdLocation.getLongitude()+","+bdLocation.getLatitude();
            HttpGet httpRequest = new HttpGet(url);

            okHttpManager.async_get(url, new HttpResponse() {
                @Override
                public void succesd(Call call, String response) {
                    Message message = new Message();
                    message.obj = response;
                    message.what = LOCALFRIENDS;
                    handler.sendMessage(message);
                }

                @Override
                public void failed(Call call, IOException e) {

                }
            });
        }
    }

    BaiduMap.OnMarkerClickListener marklistener = new BaiduMap.OnMarkerClickListener() {
        /**
         * 地图 Marker 覆盖物点击事件监听函数
         * @param marker 被点击的 marker
         */
        public boolean onMarkerClick(Marker marker){
            XmppConnection xmppConnection = XmppConnection.getInstance();
            VCard vCard = null;
            try {
                vCard = xmppConnection.getFriendunisInfo(marker.getTitle());
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            }
            View mView = LayoutInflater.from(MapLocationPosition.this).inflate(R.layout.map_marker_tips, null);
            TextView nickname = mView.findViewById(R.id.map_marker_tips_nickname);
            nickname.setText(vCard.getNickName());
            TextView age = mView.findViewById(R.id.map_marker_tips_age);
            //age.setText(vCard.getage());
            TextView married = mView.findViewById(R.id.map_marker_tips_marryed);
            //married.setText(vCard.getmarried());
            TextView creditvalues = mView.findViewById(R.id.map_marker_tips_credit_values);
            //creditvalues.setText(vCard.getcreditvalues());
            TextView creditmoney = mView.findViewById(R.id.map_marker_tips_credit_money);
            //creditmoney.setText(vCard.getcreditmoney());
            InfoWindow infoWindow = new InfoWindow(mView,marker.getPosition(),-30);
            mBaiduMap.showInfoWindow(infoWindow);
            System.out.println(marker.getTitle());
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.map_return:
                Intent goback = new Intent();
                setResult(1);
                finish();
                break;
            case R.id.editreport:
                mBaiduMap.snapshot(this);
                break;
                default:
                    System.out.println("MapLocationPosition:onClick");
        }
    }
    public File getFile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        File file = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        Bundle bundle = new Bundle();
        bundle.putString("image", getFile(bitmap).getPath());
        Intent sendmap = getIntent();
       /* if(preActivity != null && preActivity.equals("chat_contrue")) {
            sendmap = new Intent(this, chat_contrue.class);
        }else {
            sendmap = new Intent(this, PositionSettingActivity.class);
        }*/
        sendmap.putExtras(bundle);
        setResult(Activity.RESULT_OK,sendmap);
        finish();
    }


    //点击则发送定位
    private void onSendAddress(){

        Bundle bundle = new Bundle();
        bundle.putString("image", Base64.encodeToString(flattenBitmap(mapsnapshot),0));
        Intent sendmap;
        if(preActivity != null && preActivity.equals("chat_contrue")) {
            sendmap = new Intent(MapLocationPosition.this, chat_contrue.class);
        }else {
            sendmap = new Intent(MapLocationPosition.this, PositionSettingActivity.class);
        }
        sendmap.putExtras(bundle);
        setResult(Activity.RESULT_OK,sendmap);
        finish();
       /* //如果不延时，截图上没有marker
        mapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //截图动作
                mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap){
                        System.out.println("12111111111111111111111111111111111111111111");
                        mapsnapshot = bitmap;
                        isok = false;
                }
            });
        }
    },50);*/

   }
    BaiduMap.OnMapTouchListener listener = new BaiduMap.OnMapTouchListener() {

        /**
         * 当用户触摸地图时回调函数
         *
         * @param motionEvent 触摸事件
         */
        @Override
        public void onTouch(MotionEvent motionEvent) {
            System.out.println(motionEvent);
        }
    };

    BaiduMap.OnMapClickListener singlelistener = new BaiduMap.OnMapClickListener() {
        /**
         * 地图单击事件回调函数
         *
         * @param point 点击的地理坐标
         */
        @Override
        public void onMapClick(LatLng point) {
            Drawingline(point);
        }

        /**
         * 地图内 Poi 单击事件回调函数
         *
         * @param mapPoi 点击的 poi 信息
         */
        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            Toast.makeText(MapLocationPosition.this,""+mapPoi,Toast.LENGTH_LONG).show();
            return false;
        }
    };

    public void Drawingline(LatLng point){
        points.add(point);
        points.add(point);
        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(0xAAFF0000)
                .points(points);
        //在地图上绘制折线
        //mPloyline 折线对象
        Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        locationClient.unRegisterLocationListener(myLocationListener);
        locationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

}
