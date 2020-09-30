package com.example.yang.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yang.myapplication.R;
import com.example.yang.util.SharedPreferencedUtils;

import java.util.HashMap;
import java.util.Map;

public class AcademicCertificationActivity extends AppCompatActivity {
    public static int AcademicCODE = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.academic_caertification_xml);
        TextView comp = findViewById(R.id.academic_caertification_complication);
        EditText certificatenum = findViewById(R.id.academic_caertification_certificate_number);
        EditText name = findViewById(R.id.academic_caertification_name);
        comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = false;
                if(!TextUtils.isEmpty(certificatenum.getText()) && !TextUtils.isEmpty(name.getText())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("certificatenum", certificatenum.getText().toString());
                    map.put("name", name.getText().toString());
                    map.put("isacademic","yes");
                    SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();
                    sharedPreferencedUtils.UpdateFile(AcademicCertificationActivity.this, map);
                    result = true;
                }
                Intent intent = new Intent(AcademicCertificationActivity.this,OwnAuthenticationActivity.class);
                intent.putExtra("result",result);
                setResult(AcademicCODE,intent);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
