package com.example.yang.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yang.myapplication.R;

import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2018/3/31.
 */

public class life_installed_adapt extends BaseAdapter{
    private Context mContext;
    private List<Map<String,Object>> mData;

    public life_installed_adapt(List<Map<String,Object>> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.life_installed_gridview, parent, false);
            /*holder = ViewHolder.bind(parent.getContext(), convertView, parent, mLayoutRes, position);
            bindView(holder,getItem(position));*/
            holder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
            holder.txt_content = (TextView) convertView.findViewById(R.id.txt_icon);
            holder.img_icon.setImageResource(R.drawable.d_feizao);
            holder.txt_content.setText(mData.get(position).get("text").toString());
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /*
        * 未完成
        * */
        /*holder.img_icon.setImageResource();
        holder.txt_content.setText(null);*/
        return convertView;
    }

    private static class ViewHolder {
        public ImageView img_icon;                  //存放convertView
        public TextView txt_content;               //游标
    }

}
