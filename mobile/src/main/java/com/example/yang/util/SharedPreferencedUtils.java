package com.example.yang.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;


/**
 * Created by yangzhengqiang
 * Time：2018/7/7 7:55
 * Email：linuxcwork@163.com
 * Description：SharedPreferenced工具类
 */

public class SharedPreferencedUtils {
    public static SharedPreferences mPreference;
    private Context mcontext;

    public SharedPreferences getPreference(Context context) {
        if (mPreference == null) {
            mPreference = PreferenceManager.
                    getDefaultSharedPreferences(context);
        }
        mcontext = context;
        return mPreference;
    }

    public SharedPreferencedUtils() {

    }

    public  void setInteger(String name, int value) {
        mPreference.edit().putInt(name, value).commit();
    }

    public  int getInteger(String name, int default_i) {
        return mPreference.getInt(name, default_i);
    }

    /**
     * 设置字符串类型的配置
     */
    public  void setString(String name, String value) {
        mPreference.edit().putString(name, value).commit();
    }

    public  String getString(String name) {
        return mPreference.getString(name, null);
    }

    /**
     * 获取字符串类型的配置
     */
    public  String getString(String name, String defalt) {
        return mPreference.getString(name, defalt);
    }

    /**
     * 获取boolean类型的配置
     *
     * @param
     * @param name
     * @param defaultValue
     * @return
     */
    public  boolean getBoolean(String name,
                                     boolean defaultValue) {
        return mPreference.getBoolean(name, defaultValue);
    }

    public void UpdateFile( Context mcontext,Map<String, Object> map){
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences("userinfo", mcontext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String key : map.keySet()) {
            editor.putString(key, (String) map.get(key));
        }
        editor.commit();
    }

    public String getUserInfo( Context context,String name,String defasult){
        String info;
        SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        info = sharedPreferences.getString(name, defasult);
        return info;
    }
    /**
     * 设置boolean类型的配置
     *
     * @param
     * @param name
     * @param value
     */
    public  void setBoolean(String name, boolean value) {
        mPreference.edit().putBoolean(name, value).commit();
    }

    public  void setFloat(String name, Float value) {
        mPreference.edit().putFloat(name, value).commit();
    }

    public  Float getFloat(String name, Float value) {
        return mPreference.getFloat(name, 0);
    }

    public  void setLong( String name, Long value) {
        mPreference.edit().putLong(name, value).commit();
    }

    public  Long getLong(String name, Long defaultValue) {
        return mPreference.getLong(name, defaultValue);
    }


}
