package com.example.yang.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import q.rorbin.badgeview.QBadgeView;

import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_ROWID;


/**
 * Created by yang on 2018/3/15.
 */

public class signallistview extends BaseAdapter{

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private static Context context;

    public signallistview(Context context,List<Map<String,Object>> data){
        super();

        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
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

            new QBadgeView(context).bindTarget(zujian.image_messagelist)
                    .setBadgeGravity(Gravity.END | Gravity.TOP)
                    .setBadgeNumber(Integer.parseInt(data.get(i).get(sqlite_linkmanmss.KEY_ISNEWMESSAGE).toString()));
            zujian.text_offriendname=(TextView)view.findViewById(R.id.friend_name);
            zujian.text_oflastmessage=(TextView) view.findViewById(R.id.last_message);
            zujian.text_oflastdate=(TextView)view.findViewById(R.id.lastmessage_time);
            view.setTag(zujian);
        }else{
            zujian=(Zujian)view.getTag();
        }

        String.valueOf(data.get(i).get(sqlite_linkmanmss.KEY_ROWID));
        System.out.println(data.get(i).get(sqlite_linkmanmss.KEY_ROWID));
        String imgstring = String.valueOf(data.get(i).get(KEY_ROWID));
        if(!imgstring.isEmpty() && !imgstring.equals("0")) {
            byte[] bbyte = Base64.decode(imgstring, 0);
            Glide
                    .with(context)
                    .load(bbyte)
                    .fallback(R.drawable.defaulpict)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(120, 160)
                    .dontAnimate()
                    .centerCrop()
                    .skipMemoryCache(true) // 不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                    .into(zujian.image_messagelist);
        }
        zujian.text_offriendname.setText((String) data.get(i).get(sqlite_linkmanmss.KEY_NAME));
        zujian.text_oflastmessage.setText((String) data.get(i).get(sqlite_linkmanmss.KEY_CONTENT));
        zujian.text_oflastdate.setText((String) data.get(i).get(sqlite_linkmanmss.KEY_TIME));
        String name = (String)data.get(i).get("title");

        return view;
    }
}
