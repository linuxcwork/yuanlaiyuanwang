package com.example.yang.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yang.InterfaceClass.sendDataToAvtivityInterface;
import com.example.yang.adapter.GridAdapter;
import com.example.yang.myapplication.R;
import com.example.yang.util.MDGridRvDividerDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.ui
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/9/15 23:02
 * @change
 * @chang time
 * @class describe  选择需要发送的图片
 *****************************************************************/
public class AlbumSelectChat extends AppCompatActivity implements View.OnClickListener {
    private static Button send;
    private GridAdapter albumadapt = null;
    private List<Map<String, Object>> listItems = null;
    private ArrayList mdata = new ArrayList();
    private TextView priview;
    //需要获取的图片数量
    private static int getpictureamount;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    send.setText("发送"+"("+msg.arg1+")");

                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.funtionablumchat);
        ImageView returnpreview = findViewById(R.id.album_tips_return);
        returnpreview.setOnClickListener(this);
        send = findViewById(R.id.album_send);
        send.setOnClickListener(this);
        LinearLayout original = findViewById(R.id.album_original);
        original.setOnClickListener(this);
        priview = findViewById(R.id.album_privew_text);
        priview.setOnClickListener(this);
        RecyclerView noScrollgridview = findViewById(R.id.album_girdview);

        Intent getintent = getIntent();
        String source = getintent.getStringExtra("activity");
        if(source.equals("idcard") || source.equals("announceaddactivity")){
            getpictureamount = 1;
        } else if(source.equals("chat_contrue")){
            getpictureamount = 9;
        }

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4, OrientationHelper.VERTICAL, false);
        noScrollgridview.setLayoutManager(mLayoutManager);
        albumadapt = new GridAdapter(getApplicationContext(),getpictureamount);
        albumadapt.update();
        noScrollgridview.setAdapter(albumadapt);
        noScrollgridview.addOnScrollListener(scrollListener);
        noScrollgridview.addItemDecoration(new MDGridRvDividerDecoration(this));
        albumadapt.sendData(new sendDataToAvtivityInterface() {
            @Override
            public void dataCon(int count, ArrayList data, List<Map<String, Object>> alldata) {
                send.setText("发送" + "(" + count + ")");
                mdata = data;
                listItems = alldata;
            }
        });


    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_SETTLING:
                    //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                    try {
                        if(this != null) Glide.with(getApplicationContext()).pauseRequests();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                    try {
                        if(this != null) Glide.with(getApplicationContext()).pauseRequests();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    //当屏幕停止滚动，加载图片
                    try {
                        if(this != null) Glide.with(getApplicationContext()).resumeRequests();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    };

    @Override
    public void onClick(View view) {
        System.out.println("AlbumSelectChat onClick");
        switch (view.getId()){
            case R.id.album_tips_return:
                Intent intent_pre = getIntent();//new Intent(AlbumSelectChat.this,chat_contrue.class);
                AlbumSelectChat.this.startActivity(intent_pre);
                break;
            case R.id.album_send:
                albumSend();
                break;
            case R.id.album_original:
                break;
            case R.id.album_privew_text:
                Intent intent_pic = new Intent(AlbumSelectChat.this, PicturePreview.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("picture", mdata);
                intent_pic.putExtras(bundle);
                startActivity(intent_pic);
                break;
            case R.id.album_girdview:

                break;
        }
    }

    /**************************************************************************
     * Name     ： albumSend
     * descript ： 返回聊天界面并将数据发回给聊天界面
     * return   ： void
    ***************************************************************************/
    public void albumSend() {
        Intent intent_send = getIntent();
        Bundle bundle = new Bundle();
        System.out.println("click albumSend mdata：" + this.mdata);

        if (!mdata.isEmpty()) {
            bundle.putSerializable("picture", mdata);
            intent_send.putExtras(bundle);
        }
        AlbumSelectChat.this.setResult(Activity.RESULT_OK, intent_send);
        AlbumSelectChat.this.finish();
    }
}
