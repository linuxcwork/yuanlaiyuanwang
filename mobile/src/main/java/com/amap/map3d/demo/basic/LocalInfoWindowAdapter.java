package com.amap.map3d.demo.basic;

import android.view.View;
import android.widget.LinearLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;

/****************************************************************
 * @name MyApplication
 * @class name：com.amap.map3d.demo.basic
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/12/1 22:37
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class LocalInfoWindowAdapter implements AMap.InfoWindowAdapter,AMap.OnInfoWindowClickListener {

    public LocalInfoWindowAdapter(AMap amp)
    {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }
}
