package com.example.yang.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yang.myapplication.R;
import com.example.yang.util.SystemUtil;

public class PersonalInfoModifitionActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText info;
    private String name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info_modification_xml);
        ImageView back = findViewById(R.id.anounce_add_activity_return);
        TextView title = findViewById(R.id.personal_info_modifiction_item);
        info = findViewById(R.id.personal_info_modifiction_item_content);
        TextView cmp = findViewById(R.id.personal_info_modifiction_item_success);
        //设置提示信息
        TextView note = findViewById(R.id.personal_info_modifition_tips);
        SystemUtil.getSystemLanguage();
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        title.setText(name);
        back.setOnClickListener(this);
        cmp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.anounce_add_activity_return:
                break;
            case R.id.personal_info_modifiction_item_success:
                Intent forresult = new Intent(this,PersonalInfoActivity.class);
                forresult.putExtra("name",name);
                forresult.putExtra("info",info.getText().toString());
                setResult(Activity.RESULT_OK,forresult);
                finish();
                break;
        }
    }
}
