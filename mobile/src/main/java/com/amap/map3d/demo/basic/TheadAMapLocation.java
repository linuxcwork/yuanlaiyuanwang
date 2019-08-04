package com.amap.map3d.demo.basic;

import com.example.yang.myapplication.HttpResponse;
import com.example.yang.network.OkHttpManager;
import com.example.yang.util.UrlListdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;

/****************************************************************
 * @name MyApplication
 * @class name：com.amap.map3d.demo.basic
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/3/3 12:03
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class TheadAMapLocation {
    private UrlListdb urlListdb = new UrlListdb();
    private OkHttpManager http = new OkHttpManager();
    public TheadAMapLocation(){

    }

    /**
     * 发送位置请求，并获取返回数据*/
    public ArrayList<MapLocationPosition.MapNearbyInfo> HTTPAMapRequst(Map<String, Object> map){
        ArrayList<MapLocationPosition.MapNearbyInfo> mapinfoarrayList = null;
        //用户id号
        map.put("userid","6720181230111105");
            http.postKeyValuePaires(urlListdb.map, map, new HttpResponse() {
                @Override
                public void succesd(Call call, Map<String, Object> response) {
                    System.out.println(response.toString());
                }

                @Override
                public void failed(Call call, IOException e) {

                }
            });

        return mapinfoarrayList;
    }
}
