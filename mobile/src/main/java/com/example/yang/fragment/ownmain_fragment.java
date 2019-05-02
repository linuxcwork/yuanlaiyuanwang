package com.example.yang.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.map3d.demo.basic.ContentModel;
import com.amap.map3d.demo.basic.LocationService;
import com.amap.map3d.demo.basic.MapLBSUrl;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.example.yang.myapplication.HttpResponse;
import com.example.yang.myapplication.R;
import com.example.yang.network.OkHttpManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by yang on 2018/3/18.
 */

public class ownmain_fragment extends Fragment implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    private final String TAG = "ownmain";
    private boolean isbind = false;
    private LocationService locationService;
    private MapLBSUrl mapLBSUrl;

    private Handler handler= new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 1:
                    Log.d(TAG,"DDDDDDDDDDDDDDDDDDDDDDDDD");
                    try {
                        JSONObject json = new JSONObject(msg.obj.toString());
                        parser(json);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
    public ownmain_fragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_own_main,container,false);
        LinearLayout user_message = view.findViewById(R.id.person_id);
        LinearLayout wallet = view.findViewById(R.id.wallet);
        LinearLayout luser_safe = view.findViewById(R.id.luser_safe);
        LinearLayout set_person = view.findViewById(R.id.set_person);
        SwitchCompat postion = view.findViewById(R.id.user_postion_switch_open);
        user_message.setOnClickListener(this);
        wallet.setOnClickListener(this);
        postion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    initPostion();
                } else {
                    positionStop();
                }
            }
        });
        luser_safe.setOnClickListener(this);
        set_person.setOnClickListener(this);
        mapLBSUrl = new MapLBSUrl();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.person_id:
                //跳转到个人信息页面
                break;
            case  R.id.wallet:
                //跳转到钱包界面
                break;
            case  R.id.luser_safe:
                //安全模式设置
                break;
            case R.id.set_person:
                //软件的部分设置
                break;
                default:
                Log.e(TAG,"no this id in the active");
        }
    }

    public void initPostion(){
        Log.d(TAG,"INIT POSTION");
        Intent i = new Intent(ownmain_fragment.this.getContext(), LocationService.class);
        i.putExtra("name", "SurvivingwithAndroid");
        locationService = new LocationService(ownmain_fragment.this.getContext());
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
                /*if (isexitPosition()){
                    updateLBSPosition();
                } else {
                    AddPositionLBSFuntion();
                }*/
                Toast.makeText(ownmain_fragment.this.getContext(),bdLocation.getLatitude()+","+bdLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                double position[]={bdLocation.getLatitude(),bdLocation.getLongitude()};
                Map <String, Object> map = new HashMap<String, Object>();
                map.put("title", "00000000001");//用户id
                map.put("geotable_id", "1000005602");
                map.put("id","1117376393601121751");
                map.put("longitude",position[0]);
                map.put("latitude", position[1]);
                map.put("coord_type",1);
                map.put("icon","http://image.baidu.com/search/detail?z=0&word=%E4%BA%91%E5%B1%B1%E8%99%8E%E5%BD%B1&hs=0&pn=6&spn=0&di=0&pi=61293031769&tn=baiduimagedetail&is=0%2C0&ie=utf-8&oe=utf-8&cs=827023048%2C888123762&os=&simid=&adpicid=0&lpn=0&fm=&sme=&cg=&bdtype=-1&oriquery=&objurl=http%3A%2F%2Fb.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F6609c93d70cf3bc7c64ca199df00baa1cd112a72.jpg&fromurl=&gsm=0&catename=pcindexhot&islist=&querylist=");
                map.put("ak", "pFsB5nTTUw5l4gUKYRGgViSX7tMx2IOR");
                Send(mapLBSUrl.update_poi,map);
                /*Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", "00000000001");//用户id
                map.put("is_published", 1);
                map.put("geotype",1);
                map.put("ak", "pFsB5nTTUw5l4gUKYRGgViSX7tMx2IOR");*/

            }
        }

    };

    private void Send(String url, Map<String, Object> map){
        OkHttpManager http = new OkHttpManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                http.postKeyValuePaires(url, map, new HttpResponse() {
                    @Override
                    public void succesd(Call call, Object response) {
                        System.out.println(response.toString());
                        Message message = new Message();
                        message.obj = response.toString();
                        message.what = 1;
                        handler.sendMessage(message);
                        Looper.prepare();
                        //Toast.makeText(ownmain_fragment.this.getContext(),response.toString(),Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void failed(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(ownmain_fragment.this.getContext(),"wwwwwwwwwwwwww"+e,Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                });

            }
        }).start();
    }

    private void AddPositionLBSFuntion(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "00000000001");//用户id
        map.put("geotable_id", "1000005602");
        /*map.put("longitude",);
        map.put("latitude",);*/
        map.put("ak", "pFsB5nTTUw5l4gUKYRGgViSX7tMx2IOR");
        Send(mapLBSUrl.create_poi,map);
    }

    private void updateLBSPosition(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "00000000001");//用户id
        map.put("geotable_id", "1000005602");
        /*map.put("longitude",);
        map.put("latitude",);*/
        map.put("ak", "pFsB5nTTUw5l4gUKYRGgViSX7tMx2IOR");
        Send(mapLBSUrl.update_poi,map);
    }

    private boolean isexitPosition(){
        boolean isexit = false;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "00000000001");//用户id
        map.put("geotable_id", "1000005602");
        map.put("ak", "pFsB5nTTUw5l4gUKYRGgViSX7tMx2IOR");
        Send(mapLBSUrl.list_poi,map);
        return isexit;
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.luser_safe:
                break;
        }
    }

    /*
     * 解析返回数据
     */
    private void parser(JSONObject json) {
       /* DemoApplication app = (DemoApplication) getApplication();
        List<ContentModel> list = app.getList();*/

        try {
            //app.getListActivity().totalItem = json.getInt("total");

            JSONArray jsonArray = json.getJSONArray("contents");
            if (jsonArray != null && jsonArray.length() <= 0) {
                //Toast.makeText(context, "没有符合要求的数据", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                    Log.d(TAG,jsonObject2.getString("size"));
                    if(jsonObject2.getInt("size") != 0){
                        updateLBSPosition();
                    }else {
                        AddPositionLBSFuntion();
                    }
                    /*ContentModel content = new ContentModel();
                    content.setName(jsonObject2.getString("size"));
                    content.setName(jsonObject2.getString("title"));
                    content.setAddr(jsonObject2.getString("address"));

                    content.setDistance(jsonObject2.getString("distance") + "米");

//					double latitude = jsonObject2.getDouble("latitude");
//					double longitude = jsonObject2.getDouble("longitude");
                    JSONArray locArray = jsonObject2.getJSONArray("location");
                    double latitude = locArray.getDouble(1);
                    double longitude = locArray.getDouble(0);
                    content.setLatitude(latitude);
                    content.setLongitude(longitude);

                    float results[] = new float[1];

                    if (app.currlocation != null) {
                        Location.distanceBetween(
                                app.currlocation.getLatitude(),
                                app.currlocation.getLongitude(), latitude,
                                longitude, results);
                    }
                    content.setDistance((int) results[0] + "米");

//					JSONObject jsonObject3 = jsonObject2.getJSONObject("ext");
//					content.setPrice(jsonObject3.getString("dayprice"));
//					content.setImageurl(jsonObject3.getString("mainimage"));
//					content.setWebUrl(jsonObject3.getString("roomurl"));
//					int leasetype = jsonObject3.getInt("leasetype");
                    content.setPrice(jsonObject2.getString("dayprice"));
                    content.setImageurl(jsonObject2.getString("mainimage"));
                    content.setWebUrl(jsonObject2.getString("roomurl"));
                    int leasetype = jsonObject2.getInt("leasetype");


                    String[] categoryArray = context.getResources()
                            .getStringArray(R.array.category);
                    if(leasetype > categoryArray.length -1){
                        leasetype = 1;
                    }
                    content.setLeaseType(categoryArray[leasetype]);
                    list.add(content);*/


                }

            }
            /*if (list.size() < 10) {
                app.getListActivity().getListView()
                        .removeFooterView(app.getListActivity().loadMoreView);
            }*/
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       /* ContentAdapter adapter = ((DemoApplication) getApplication())
                .getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            app.getListActivity().loadMoreView.setVisibility(View.VISIBLE);
            app.getListActivity().progressBar.setVisibility(View.INVISIBLE);
        }
        if (app.getMapActivity() != null) {
            app.getMapActivity().removeAllMarker();
            app.getMapActivity().addAllMarker();
        }*/
    }
}
