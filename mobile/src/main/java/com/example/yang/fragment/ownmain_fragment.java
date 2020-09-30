package com.example.yang.fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yang.Activity.BluetoothSettingActivity;
import com.example.yang.Activity.OwnAuthenticationActivity;
import com.example.yang.Activity.OwnCreditMoneyActivity;
import com.example.yang.Activity.OwnCreditValueActivity;
import com.example.yang.Activity.OwnParticipateInActivities;
import com.example.yang.Activity.PersonalInfoActivity;
import com.example.yang.Activity.PositionSettingActivity;
import com.example.yang.Activity.SettingActivity;
import com.example.yang.Activity.SettingMarryCondition;
import com.example.yang.myapplication.R;
import com.example.yang.util.SharedPreferencedUtils;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by yang on 2018/3/18.
 */

public class ownmain_fragment extends Fragment implements View.OnClickListener {

    private final String TAG = "ownmain_fragment";
    private boolean isbind = false;
    private SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();

	
    public ownmain_fragment(){

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_own_main,container,false);
        LinearLayout user_message = view.findViewById(R.id.person_id);
        LinearLayout wallet = view.findViewById(R.id.wallet);
        LinearLayout set_person = view.findViewById(R.id.set_person);
        RoundedImageView headimage = view.findViewById(R.id.fragment_own_head_image);
        SharedPreferencedUtils sharedPreferenced = new SharedPreferencedUtils();;
        String imagepath = sharedPreferenced.getUserInfo(getActivity(),"headsculpture",null);
        Glide
                .with(ownmain_fragment.this)
                .load(imagepath)
                .dontAnimate()
                .centerCrop()
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(headimage);

        TextView stagename = view.findViewById(R.id.user_name);
        stagename.setText(sharedPreferenced.getUserInfo(getActivity(),"stagename","小舟"));
        TextView marrigestate = view.findViewById(R.id.user_marriage);
        LinearLayout authentication = view.findViewById(R.id.fragment_own_authentication_linearLayout);
        authentication.setOnClickListener(this);
        TextView authenticationstate = view.findViewById(R.id.fragment_own_authentication_state);
        authenticationstate.setText(sharedPreferenced.getUserInfo(getActivity(),"authenticationstate","小舟"));
        LinearLayout credit_value = view.findViewById(R.id.fragment_own_credit_value_linearLayout);
        credit_value.setOnClickListener(this);
        TextView credit_valueamount = view.findViewById(R.id.fragment_own_credit_value_amount);
        credit_valueamount.setText(sharedPreferenced.getUserInfo(getActivity(),"credit_valueamount","100"));
        LinearLayout credit_money = view.findViewById(R.id.fragment_own_credit_money_linearLayout);
        credit_money.setOnClickListener(this);
        TextView credit_moneyamount = view.findViewById(R.id.fragment_own_credit_money_amount);
        credit_moneyamount.setText(sharedPreferenced.getUserInfo(getActivity(),"credit_moneyamount","10000"));
        marrigestate.setText(sharedPreferenced.getUserInfo(getActivity(),"marriage","no"));
        user_message.setOnClickListener(this);
        wallet.setOnClickListener(this);

        RelativeLayout position = view.findViewById(R.id.own_fragment_position_relativelayout);
        RelativeLayout bluset = view.findViewById(R.id.own_fragment_nearby_personal_relativelayout);
        RelativeLayout marrycon = view.findViewById(R.id.own_fragment_marry_condition_relativelayout);
        RelativeLayout participate = view.findViewById(R.id.own_fragment_participate_in_activities_relativelayout);
        participate.setOnClickListener(this);
        marrycon.setOnClickListener(this);
        position.setOnClickListener(this);
        bluset.setOnClickListener(this);
        set_person.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.person_id:
                //跳转到个人信息页面
                Intent perinfo = new Intent(getContext(), PersonalInfoActivity.class);
                startActivity(perinfo);
                break;
            case  R.id.wallet:
                //跳转到钱包界面
                break;

            case R.id.set_person:
                //软件的部分设置
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.own_fragment_position_relativelayout:
                Intent intentposition = new Intent(getContext(), PositionSettingActivity.class);
                startActivity(intentposition);
                break;
            case R.id.own_fragment_nearby_personal_relativelayout:
                Intent intentbluset = new Intent(getContext(), BluetoothSettingActivity.class);
                startActivity(intentbluset);
                break;
            case R.id.fragment_own_authentication_linearLayout:
                Intent authintent = new Intent(getContext(), OwnAuthenticationActivity.class);
                startActivity(authintent);
                break;
            case R.id.fragment_own_credit_value_linearLayout:
                Intent creditvalueintent = new Intent(getContext(), OwnCreditValueActivity.class);
                startActivity(creditvalueintent);
                break;
            case R.id.fragment_own_credit_money_linearLayout:
                Intent creditmonintent = new Intent(getContext(), OwnCreditMoneyActivity.class);
                startActivity(creditmonintent);
                break;
            case R.id.own_fragment_marry_condition_relativelayout:
                Intent marryconintent = new Intent(getContext(), SettingMarryCondition.class);
                startActivity(marryconintent);
                break;
            case R.id.own_fragment_participate_in_activities_relativelayout:
                Intent parintent = new Intent(getContext(), OwnParticipateInActivities.class);
                startActivity(parintent);
                break;
                default:
                Log.e(TAG,"no this id in the active");
        }
    }

}
