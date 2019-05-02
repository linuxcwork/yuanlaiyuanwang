package com.example.yang.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yang.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.ui
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/10/4 12:08
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class PicturePreview extends AppCompatActivity implements View.OnClickListener{
    private ImageView imagereturn;
    private TextView  imagepostion;
    private Button    imagesend;
    private ImageView imagecurr;
    private GridView  imageall;
    private TextView  imageedit;
    private CheckBox  imagecbp;

    private int position;
    private int allcount;
    private int mcount = 0;

    private int SELECTED_PICTURE = 1;

    private List<Map<String, Object>> selectlist = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("PicturePreview onCreate");
        setContentView(R.layout.picture_preview);
        initActivity();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList alldata =(ArrayList) bundle.getSerializable("picture");
        //allcount = alldata.size();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("PicturePreview new thread");
                /*for (int i = 0;!alldata.isEmpty();i++){
                    if (alldata.get(i).get("state").equals(true)){
                        Map<String, Object> map = new HashMap<>();
                        map.put("postion",i);
                        map.put("picture",alldata.get(i).get("name").toString());
                        map.put("count",mcount);
                        mcount++;
                        selectlist.add(map);
                    }*/
                //}
                /*Message message = new Message();
                message.what = SELECTED_PICTURE;
                message.obj = selectlist;
                handler.sendMessage(message);*/
            }
        }).start();

    }

    public void initActivity(){
        imagereturn  = findViewById(R.id.image_return);
        imagereturn.setOnClickListener(this);
        imagepostion = findViewById(R.id.image_position);
        imagepostion.setOnClickListener(this);
        imagesend    = findViewById(R.id.image_count);
        imagesend.setOnClickListener(this);
        imagecurr    = findViewById(R.id.image_curr);
        imagecurr.setOnClickListener(this);
        imageall     = findViewById(R.id.image_all);
        imageedit    = findViewById(R.id.image_edit);
        imageedit.setOnClickListener(this);
        imagecbp     = findViewById(R.id.image_cbp);
        imagecbp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }
}
