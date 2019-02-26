package com.example.yang.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2018/3/24.
 */

public class Linkman_Listview_Adapt extends BaseAdapter{
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private static Context context;

    public Linkman_Listview_Adapt(Context context,List<Map<String,Object>> data){
        super();

        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public void maeeage_onClick(View view,int i) {
        /*String name = (String) data.get(i).get("title");
        Bundle bundle = new Bundle();
        bundle.putString("key", name);

        Intent intent = new Intent(context,chat_contrue.class);
        intent.putExtras(bundle);
        this.context.startActivity(intent);*/

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
        zujia zujia=null;
        if(view==null){
            zujia=new zujia();
            //获得组件，实例化组件
            view=layoutInflater.inflate(R.layout.linkman_listview, null);
            zujia.image_messagelist=(ImageView)view.findViewById(R.id.any_friend_image);
            zujia.text_offriendname=(TextView)view.findViewById(R.id.any_friend_textview);
            view.setTag(zujia);
        }else{
            zujia=(zujia)view.getTag();
        }
        //绑定数据

        zujia.image_messagelist.setBackgroundResource((Integer)data.get(i).get("image"));
        zujia.text_offriendname.setText((String)data.get(i).get("title"));

        zujia.image_messagelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maeeage_onClick(view,i);
            }
        });
        zujia.text_offriendname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maeeage_onClick(view,i);
            }
        });
        return view;
    }

    public final class zujia {
        public ImageView image_messagelist;
        public TextView text_offriendname;
    }
}
