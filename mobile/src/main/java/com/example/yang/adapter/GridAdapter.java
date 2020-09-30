package com.example.yang.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yang.InterfaceClass.sendDataToAvtivityInterface;
import com.example.yang.myapplication.R;
import com.example.yang.Activity.PicturePreview;
import com.example.yang.util.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.adapter
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/1/6 19:13
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class GridAdapter extends RecyclerView.Adapter<ViewHolder>{
    private LayoutInflater inflater;
    private ArrayList pathtemp = new ArrayList();

    //已选择图片数量
    private int mcountview;
    private Cursor cursor;

    private Context mcontext;
    private int getpictureamount = 0;
    private ViewHolder viewHolder;
    private List<Map<String, Object>> listItemsAdpt = new ArrayList<>();
    private sendDataToAvtivityInterface senddatatoavtivityinterface;

    /**************************************************************************
     * Name     ： GridAdapter
     * descript ： 适配器构造器
     * return   ： 无
     ***************************************************************************/
    public GridAdapter(Context context,int count) {
        mcontext = context;
        inflater = LayoutInflater.from(context);
        cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null);
        mcountview = 0;
        this.getpictureamount = count;
    }

    public void sendData(sendDataToAvtivityInterface sendDataToAvtivityInterface){
        this.senddatatoavtivityinterface = sendDataToAvtivityInterface;
    }


    public void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = null;
                System.out.println(cursor.getCount());
                ArrayList names = new ArrayList();
                ArrayList fileNames = new ArrayList();
                for (int i = 0; cursor.moveToNext(); i++) {
                    //获取图片的名称
                    name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    //获取图片的生成日期
                    byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    //获取图片的详细信息
                    names.add(name);
                    fileNames.add(new String(data, 0, data.length - 1));
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("state", false);
                    listItemsAdpt.add(map);
                }
            }
        }).start();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.album_girdview, parent, false);
        viewHolder = new ViewHolder(convertView);

        viewHolder.mImg = (ImageView) convertView.findViewById(R.id.picture_album);
        viewHolder.checkBox = convertView.findViewById(R.id.select_checkbox);
        viewHolder.checkBox.setOnCheckedChangeListener(compoundButton);
        return viewHolder;
    }

    private CompoundButton.OnCheckedChangeListener compoundButton=new CompoundButton.OnCheckedChangeListener(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int) buttonView.getTag();
            if (listItemsAdpt.get(position).get("state").equals(true)) {
                mcountview = mcountview - 1;
                listItemsAdpt.get(position).put("state", false);
                //holder.checkBox.setChecked(false);
                pathtemp.remove(listItemsAdpt.get(position).get("name"));
                senddatatoavtivityinterface.dataCon(mcountview, pathtemp, listItemsAdpt);
            } else if (mcountview < getpictureamount) {
                mcountview = mcountview + 1;
                listItemsAdpt.get(position).put("state", true);
                pathtemp.add(listItemsAdpt.get(position).get("name"));
                senddatatoavtivityinterface.dataCon(mcountview, pathtemp, listItemsAdpt);
            } else {
                buttonView.setChecked(false);
            }
        }
    };

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (listItemsAdpt.size() == 0) {
            return;
        }
        Glide
                .with(mcontext)
                .load(listItemsAdpt.get(position).get("name").toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(200, 400)
                .dontAnimate()
                .centerCrop()
                .into(holder.mImg);
        holder.checkBox.setChecked(listItemsAdpt.get(position).get("state").equals(true));
        holder.mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_pic = new Intent(mcontext, PicturePreview.class);
                Bundle bundle = new Bundle();
                ArrayList pathtempclk = new ArrayList();
                pathtempclk.add(listItemsAdpt.get(position).get("name").toString());
                bundle.putSerializable("picture",pathtempclk);
                intent_pic.putExtras(bundle);
                mcontext.startActivity(intent_pic);
            }
        });
        holder.checkBox.setTag(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

}
