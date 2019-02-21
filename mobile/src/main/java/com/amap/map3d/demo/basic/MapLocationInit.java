package com.amap.map3d.demo.basic;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/****************************************************************
 * @name MyApplication
 * @class name：com.amap.map3d.demo.basic
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/12/1 14:37
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class MapLocationInit {
    private Context fathercontext;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //纬度
    private double Latitude;
    //经度
    private double Longitude;
    //精度
    private float Accuracy;
    //地址描述
    private String Address;
    //国家
    private String Country;
    //省
    private String Province;
    //城市
    private String city;

    public MapLocationInit(Context mcontext){
        this.fathercontext = mcontext;
    }

    /**************************************************************************
     * Name ：     initLocation
     * descript ： 被MapLoactionPosition 调用
     *              初始化定位功能
     * return ：   无返回值
    ***************************************************************************/
    public void initLocation()
    {
        AMapLocation amaplocation;
        //声明定位回调监听器
        AMapLocationListener mLocationListener = new AMapLocationListener(){

            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(fathercontext);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        /*mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if(null != locationClient){
            locationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            locationClient.stopLocation();
            locationClient.startLocation();
        }*/

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

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


}
