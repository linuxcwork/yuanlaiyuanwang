package com.example.yang.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.yang.adapter.ThoughtAdapt;
import com.example.yang.myapplication.HttpResponse;
import com.example.yang.myapplication.MainActivity;
import com.example.yang.myapplication.R;
import com.example.yang.network.OkHttpManager;
import com.example.yang.util.UrlListdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class LandOfThought extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "LandOfThought";
    private RecyclerView recyclerView;
    private List<Map<String, Object>> mDatas;
    private OkHttpManager okHttpManager = new OkHttpManager();
    private List<Map<String, Object>> listall=new ArrayList<Map<String,Object>>();

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String response =(String) msg.obj;
            getData(response);
            return false;
        }
	});
        
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.land_of_thought);
        recyclerView = (RecyclerView) findViewById(R.id.thought_content);
        FloatingActionButton floatingActionButton = findViewById(R.id.thought_fab);
        ImageView thought_return = findViewById(R.id.thought_main_return);
        thought_return.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
        RequestThoughtData();
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.thought_fab:
                 Intent intenteditor = new Intent(this, RichEditorText.class);
                 startActivity(intenteditor);
                 break;
             case R.id.thought_main_return:
                 Intent return_intent = new Intent(this, MainActivity.class);
                 return_intent.putExtra("num",1);
                 startActivity(return_intent);
                 break;
                 default:

         }
    }

    private void getData(String response){
        //mDatas = okHttpManager.getJsonArray(response);
        mDatas = getData();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper. VERTICAL);
        //设置Adapter
        ThoughtAdapt recycleAdapter = new ThoughtAdapt(mDatas);
        recyclerView.setAdapter(recycleAdapter);
        //设置分隔线
        recyclerView.addItemDecoration( new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator( new DefaultItemAnimator());
    }

    private List<Map<String, Object>> getData(){
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("title", "ffffffffffff");
            map.put("user", "这是一个标题"+i);
            map.put("grade", "这是一个详细信息" + i);
            map.put("time","444444444");
            listall.add(map);
        }

        return listall;
    }

    private void RequestThoughtData(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<String, Object> map = new HashMap<>();

                    UrlListdb url = new UrlListdb();
                   okHttpManager.postKeyValuePaires(url.thoughtfileUrl, map, new HttpResponse(){

                       @Override
                       public void succesd(Call call, String response) {
                            Message message = new Message();
                            message.obj = response;
                            handler.sendMessage(message);
                       }

                       @Override
                       public void failed(Call call, IOException e) {
                           Log.e(TAG,"request error "+e);
                       }
                   });
                }
            }).start();
    }

}
