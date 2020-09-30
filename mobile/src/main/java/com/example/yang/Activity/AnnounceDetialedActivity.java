package com.example.yang.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yang.myapplication.R;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.Activity
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/10/13 20:21
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class AnnounceDetialedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announce_detialed_information);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ImageView delimage = findViewById(R.id.announce_add_activity_bill);
        Glide
                .with(this)
                .load(bundle.get("image"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(120, 160)
                .dontAnimate()
                .centerCrop()
                .into(delimage);
        TextView title = findViewById(R.id.announce_detialed_activity_title);
        title.setText((String)bundle.get("title"));
        TextView sponsor = findViewById(R.id.announce_detialed_activity_sponsor);
        sponsor.setText((String)bundle.get("sponsor"));
        TextView startdate = findViewById(R.id.announce_detialed_activity_date_start);
        startdate.setText((String)bundle.get("startdate"));
        TextView enddate = findViewById(R.id.announce_detialed_activity_date_end);
        enddate.setText((String)bundle.get("enddate"));
        TextView starttime = findViewById(R.id.announce_detialed_activity_time_start);
        starttime.setText((String)bundle.get("starttime"));
        TextView endtime = findViewById(R.id.announce_detialed_activity_time_end);
        endtime.setText((String)bundle.get("endtime"));
        TextView addr = findViewById(R.id.announce_detialed_activity_addr);
        addr.setText((String)bundle.get("addr"));
        TextView tel = findViewById(R.id.announce_detialed_activity_telephone);
        tel.setText((String)bundle.get("tel"));
        TextView charge = findViewById(R.id.announce_detialed_activity_charge);
        if(bundle.get("charge") != null){
            charge.setText((String)bundle.get("charge"));
        }
        TextView note = findViewById(R.id.announce_detialed_activity_note);
        note.setText((String)bundle.get("note"));

        Button cancle = findViewById(R.id.announce_detialed_activity_cancel);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器发送取消请求
                //将该信息从个人信息列表中删除
                //将是能按钮置灰
            }
        });
        Button publish = findViewById(R.id.announce_detialed_activity_publish);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器发送添加请求
                // 将该信息保存在本地个人信息界面可以查看
                //使能取消按钮
                //将添加按钮置灰
            }
        });
    }
}
