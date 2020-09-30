package com.example.yang.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yang.Activity.PDFReadActivity;
import com.example.yang.myapplication.R;

import java.util.List;
import java.util.Map;

public class ThoughtAdapt extends RecyclerView.Adapter<ThoughtAdapt.VH> implements View.OnClickListener {
    private ViewGroup parent;
    @Override
    public void onClick(View v) {

    }

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{
        public final TextView title;
        public final TextView user;
        public final TextView grade;
        public final TextView time;
        public VH(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.thought_title);
            user = (TextView) v.findViewById(R.id.thought_username);
            grade = (TextView) v.findViewById(R.id.thought_grade);
            time = (TextView) v.findViewById(R.id.thought_time);
        }
    }

    private List<Map<String, Object>> mDatas;
    public ThoughtAdapt(List<Map<String, Object>> data) {
        this.mDatas = data;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.title.setText(mDatas.get(position).get("title").toString());
        holder.user.setText(mDatas.get(position).get("user").toString());
        holder.grade.setText(mDatas.get(position).get("grade").toString());
        holder.time.setText(mDatas.get(position).get("time").toString());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), PDFReadActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title",mDatas.get(position).get("title").toString());
                bundle.putString("user",mDatas.get(position).get("user").toString());
                bundle.putString("grade",mDatas.get(position).get("grade").toString());
                bundle.putString("time",mDatas.get(position).get("time").toString());
                intent.putExtra("name",bundle);
                parent.getContext().startActivity(intent);
            }
        });
        holder.user.setOnClickListener(this);
        holder.grade.setOnClickListener(this);
        holder.time.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        this.parent = parent;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thoughtrecycleadapt, parent, false);
        return new VH(v);
    }
}
