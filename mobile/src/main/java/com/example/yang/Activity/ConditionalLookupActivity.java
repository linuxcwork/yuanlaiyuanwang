package com.example.yang.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.yang.myapplication.MainActivity;
import com.example.yang.myapplication.R;

import java.util.HashMap;
import java.util.Map;

public class ConditionalLookupActivity extends AppCompatActivity {

    private Map<String, Object> params = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conditional_lookup_activity);
        initView();

    }

    private  void initView(){
        ImageView returnimage = findViewById(R.id.conditional_lookup_activity_return);
        TextView title = findViewById(R.id.conditional_lookup_activity_position_text);
        RadioGroup sex = findViewById(R.id.conditional_lookup_activity_rediobutton_sex);
        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.conditional_lookup_activity_rediobutton_man:
                        params.put("sex","man");
                        break;
                    case R.id.conditional_lookup_activity_rediobutton_woman:
                        params.put("sex","woman");
                        break;
                }
            }
        });
        EditText minage = findViewById(R.id.conditional_lookup_activity_age_min);
        EditText maxage = findViewById(R.id.conditional_lookup_activity_age_max);
        EditText mincred = findViewById(R.id.conditional_lookup_activity_credit_min);
        EditText maxcred = findViewById(R.id.conditional_lookup_activity_credit_max);
        EditText minstg = findViewById(R.id.conditional_lookup_activity_strength_min);
        EditText maxstg = findViewById(R.id.conditional_lookup_activity_strength_max);
        EditText minhgt = findViewById(R.id.conditional_lookup_activity_height_min);
        EditText maxhgt = findViewById(R.id.conditional_lookup_activity_height_max);
        EditText minwgt = findViewById(R.id.conditional_lookup_activity_weight_min);
        EditText maxwgt = findViewById(R.id.conditional_lookup_activity_weight_max);
        Button   lookup = findViewById(R.id.conditional_lookup_activity_lookup);

        returnimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent return_pre = new Intent(ConditionalLookupActivity.this, MainActivity.class);
                return_pre.putExtra("num",1);
                startActivity(return_pre);
            }
        });
        lookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("smallage",minage.getText().toString());
                params.put("largeage",maxage.getText().toString());
                params.put("mincred",mincred.getText().toString());
                params.put("maxcred",maxcred.getText().toString());
                params.put("minstg",minstg.getText().toString());
                params.put("maxstg",maxstg.getText().toString());
                params.put("minhgt",minhgt.getText().toString());
                params.put("maxhgt",maxhgt.getText().toString());
                params.put("minwgt",minwgt.getText().toString());
                params.put("maxwgt",maxwgt.getText().toString());
                //发送到服务器
            }
        });
    }

}
