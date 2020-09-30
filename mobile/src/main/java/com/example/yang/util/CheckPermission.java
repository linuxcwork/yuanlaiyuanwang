package com.example.yang.util;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;


/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.util
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/2/19 20:38
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class CheckPermission {

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SEND_SMS};
    private static String[] POSITION_PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static int REQUEST_EXTERNAL_STORAGE = 1;

    public CheckPermission(){

    }

    /**
    * 判断是否已获取存储权限
     * */
    public static final boolean isStorage(final Activity activity){
        // Check if we have write permission
        int wrt = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int rnd = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int ph_state = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE);
        int cmr = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);
        int aud = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);
        if (wrt != PackageManager.PERMISSION_GRANTED ||
                rnd != PackageManager.PERMISSION_GRANTED ||
                ph_state != PackageManager.PERMISSION_GRANTED ||
                cmr != PackageManager.PERMISSION_GRANTED ||
                aud != PackageManager.PERMISSION_GRANTED) {
        // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
        return false;
    }

    public static final void isGPSpermission(Activity activity){
        int wifi_ops = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_WIFI_STATE);
        int net_ops = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int gps_ops = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (wifi_ops != PackageManager.PERMISSION_GRANTED ||
                net_ops != PackageManager.PERMISSION_GRANTED ||
                gps_ops != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,POSITION_PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isGPSOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkBluetooth(Context mcontext) {
        if (!mcontext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        return true;
    }



}
