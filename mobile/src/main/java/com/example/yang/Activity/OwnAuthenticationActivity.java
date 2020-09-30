package com.example.yang.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.yang.Aichar.IdCardIdtfcation;
import com.example.yang.myapplication.R;
import com.example.yang.util.SharedPreferencedUtils;
import com.wildma.idcardcamera.camera.IDCardCamera;


public class OwnAuthenticationActivity extends AppCompatActivity {
    private ImageView isrealname;
    private ImageView isacademic;

    private final int REQUESTACACODE = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.own_authentication_list_xml);
        //实名制
        RelativeLayout realname = findViewById(R.id.authentication_list_real_name_relativelayout);
        isrealname = findViewById(R.id.authentication_list_real_name_is);
        SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();
        String isrealnamestring = sharedPreferencedUtils.getUserInfo(this,"isrealname","no");
        if(isrealnamestring != null && isrealnamestring.equals("yes")){
            isrealname.setVisibility (View.VISIBLE);
        }

        realname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //https://github.com/wildma/IDCardCamera/blob/master/idcardcamera/src/main/res/layout/activity_camera.xml
                IDCardCamera.create(OwnAuthenticationActivity.this).openCamera(IDCardCamera.TYPE_IDCARD_FRONT);
            }
        });

        //学历认证
        RelativeLayout academic = findViewById(R.id.authentication_list_academic_certification_relativelayout);
        isacademic = findViewById(R.id.authentication_list_academic_certification_is);
        String isacademicstring = sharedPreferencedUtils.getUserInfo(this,"isacademic","no");
        if(isacademicstring != null && isacademicstring.equals("yes")){
            isacademic.setVisibility(View.VISIBLE);
        }

        academic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent academicintent = new Intent(OwnAuthenticationActivity.this,AcademicCertificationActivity.class);
                startActivityForResult(academicintent,REQUESTACACODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == IDCardCamera.RESULT_CODE) {
            //获取图片路径，显示图片
            final String path = IDCardCamera.getImagePath(data);
            if (!TextUtils.isEmpty(path)) {
                if (requestCode == IDCardCamera.TYPE_IDCARD_FRONT) { //身份证正面
                    IdCardIdtfcation idCardIdtfcation = new IdCardIdtfcation(this);
                    boolean result = idCardIdtfcation.identificationAction(path,this,"front");
                    if(result){
                        isrealname.setVisibility(View.VISIBLE);
                    }
                }
            }
        }else if(resultCode == AcademicCertificationActivity.AcademicCODE){
            if(requestCode == REQUESTACACODE){
                if(data != null && data.getBooleanExtra("result",false)){
                    isacademic.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
