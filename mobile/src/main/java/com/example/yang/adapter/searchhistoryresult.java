package com.example.yang.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yang.myapplication.R;
import com.example.yang.myapplication.sqlite_linkmanmss;

import java.util.List;
import java.util.Map;

public class searchhistoryresult extends BaseAdapter {

    private Context mContext;
    private List<Map<String,String>> mData;

    public searchhistoryresult(Context context, List<Map<String,String>> data)
    {
        this.mContext=context;
        this.mData=data;
    }

    public void Refresh()
    {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int Index)
    {
        return mData.get(Index);
    }

    @Override
    public long getItemId(int Index)
    {
        return Index;
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int Index, View mView, ViewGroup mParent)
    {
        String msg = mData.get(Index).get(sqlite_linkmanmss.KEY_CONTENT);
        String time = mData.get(Index).get(sqlite_linkmanmss.KEY_TIME);

        mView = LayoutInflater.from(mContext).inflate(R.layout.searchchatresultxml, null);
        TextView msgtext = mView.findViewById(R.id.search_chat_his_result_msg);
        msgtext.setText(msg);
        TextView timetext = mView.findViewById(R.id.search_chat_his_result_time);
        timetext.setText(time);
        return mView;
    }

}
