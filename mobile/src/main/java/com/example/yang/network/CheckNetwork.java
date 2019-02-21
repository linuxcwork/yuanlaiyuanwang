package com.example.yang.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.network
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/11/25 20:13
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class CheckNetwork {
    public CheckNetwork()
    {
        System.out.println("CheckNetwork: CheckNetwork");
    }

    /**************************************************************************
     * Name ：     isNetworkConnected
     * descript ： 判断是否有网络连接
     * return ：   true or false
    ***************************************************************************/
    public boolean isNetworkConnected(Context context)
    {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**************************************************************************
     * Name ：     isWifiConnected
     * descript ： 判断WIFI是否连接
     * return ：   true or false
    ***************************************************************************/
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**************************************************************************
     * Name ：      isMobileConnected
     * descript ：  判断当前MOBILE连接是否可用
     * return ：    true or false
    ***************************************************************************/
    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**************************************************************************
     * Name ：     getConnectedType
     * descript ： 获取当前网络连接类型
     * return ：
    ***************************************************************************/
    public int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }
}
