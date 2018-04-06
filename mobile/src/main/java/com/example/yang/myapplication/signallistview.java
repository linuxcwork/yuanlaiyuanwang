package com.example.yang.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by yang on 2018/3/15.
 */

public class signallistview extends BaseAdapter{

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private static Context context;

    public callback mcallback;
    public signallistview(Context context,List<Map<String,Object>> data){
        super();

        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);

    }



    public void maeeage_onClick(View view,int i) {
        String name = (String) data.get(i).get("title");
       Bundle bundle = new Bundle();
       bundle.putString("key", name);

        Intent intent = new Intent(context,chat_contrue.class);
        intent.putExtras(bundle);
        this.context.startActivity(intent);
       /*Toast.makeText(context,"hashish's",Toast.LENGTH_LONG).show();*/
        /*mcallback.onItemWidgeClickListener(view);*/
    }

    public final class Zujian{
        public ImageView image_messagelist;
        public TextView text_offriendname;
        public TextView text_oflastmessage;
        public TextView text_oflastdate;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Zujian zujian=null;
        if(view==null){
            zujian=new Zujian();
            //获得组件，实例化组件
            view=layoutInflater.inflate(R.layout.listviewcmc, null);
            zujian.image_messagelist=(ImageView)view.findViewById(R.id.img);
            zujian.text_offriendname=(TextView)view.findViewById(R.id.friend_name);
            zujian.text_oflastmessage=(TextView) view.findViewById(R.id.last_message);
            zujian.text_oflastdate=(TextView)view.findViewById(R.id.lastmessage_time);
            view.setTag(zujian);
        }else{
            zujian=(Zujian)view.getTag();
        }
        //绑定数据
        SimpleDateFormat simpleFormatter = new SimpleDateFormat();
        Date message_time = new Date(System.currentTimeMillis());

        zujian.image_messagelist.setBackgroundResource((Integer)data.get(i).get("image"));
        zujian.text_offriendname.setText((String)data.get(i).get("title"));
        zujian.text_oflastmessage.setText((String)data.get(i).get("info"));
        zujian.text_oflastdate.setText(simpleFormatter.format(message_time));

        zujian.image_messagelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maeeage_onClick(view,i);
            }
        });
        zujian.text_oflastdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maeeage_onClick(view,i);
            }
        });
        zujian.text_oflastmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maeeage_onClick(view,i);
            }
        });
        zujian.text_offriendname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maeeage_onClick(view,i);
            }
        });


        return view;
    }
}
