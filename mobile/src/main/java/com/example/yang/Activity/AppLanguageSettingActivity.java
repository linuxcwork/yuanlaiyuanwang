package com.example.yang.Activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.yang.adapter.AppLanguageAdapt;
import com.example.yang.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppLanguageSettingActivity extends AppCompatActivity {
    private String[]  lanlist={"跟随系统","简体中文","繁體中文","English","Bahasa Indonesia","Bahasa Melayu","Espanol","বাংলা ভাষার","Português","Deutsch","日本語","한국어","русский","Le français"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_language_setting_xml);
        ListView lan = findViewById(R.id.app_language_listview);
        AppLanguageAdapt appLanguageAdapt = new AppLanguageAdapt(this, getData());
        lan.setAdapter(appLanguageAdapt);
        lan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppLanguageAdapt.setmPosition(position);
                lan.setAdapter(appLanguageAdapt);
                switchLanguage(view.getId());
            }
        });
    }

    private List<String> getData(){
        List<String> list = new ArrayList<>();
        for(String lan:lanlist){
            list.add(lan);
        }
        return list;
    }

    //选择设置语言
    public void switchLanguage(int id){
        switch (id){
            case 0:
                followSystem();
                break;
            case 1:
                setLanguage(Locale.SIMPLIFIED_CHINESE);
                break;
            case 2:
                setLanguage(Locale.TRADITIONAL_CHINESE);
                break;
            case 3:
                setLanguage(Locale.ENGLISH);
                break;
            case 4:
                setLanguage(new Locale("in_ID"));
                break;
            case 5:
                setLanguage(new Locale("ms_MY"));
                break;
            case 6:
                setLanguage(new Locale("es"));
                break;
            case 7:
                setLanguage(new Locale("bn_BD"));
                break;
            case 8:
                setLanguage(new Locale("pt_PT"));
                break;
            case 9:
                setLanguage(new Locale("de_DE"));
                break;
            case 10:
                setLanguage(new Locale("ja_JP"));
                break;
            case 11:
                setLanguage(new Locale("ko_KR"));
                break;
            case 12:
                setLanguage(new Locale("sk_SK"));
                break;
            case 13:
                setLanguage(new Locale("fr_FR"));
                break;
        }
    }

    private void setLanguage(Locale locale){
        // Locale.setDefault(locale);
        //设置语言类型
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        // 应用用户选择语言
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        }else{
            configuration.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(new LocaleList(locale));
            createConfigurationContext(configuration);
        }else{
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
    }

    public void followSystem(){
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String local = Locale.getDefault().toString();
        String country =getResources().getConfiguration().locale.getCountry();
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
       //该方法已经废弃，官方建议使用 Context.createConfigurationContext(Configuration)
        resources.updateConfiguration(configuration, metrics);
    }
}
