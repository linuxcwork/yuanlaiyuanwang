package com.example.yang.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.ui.GifTextView;

import java.util.List;

import static com.example.yang.myapplication.R.drawable.d_ganmao;

/**
 * Created by yang on 2018/4/6.
 */

public class chat_adapt extends BaseAdapter{
    private Context mContext;
    private List<chat_contrue.WeChatMessage> mData;

    public chat_adapt(Context context,List<chat_contrue.WeChatMessage> data)
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
        TextView Content;
        switch(mData.get(Index).getType())
        {
            case chat_contrue.WeChatMessage.MessageType_Time:
                mView= LayoutInflater.from(mContext).inflate(R.layout.chat_room_time, null);
                Content=(TextView)mView.findViewById(R.id.Time);
                Content.setText(mData.get(Index).getMessageTime());
                break;
            case chat_contrue.WeChatMessage.MessageType_From:
                mView=LayoutInflater.from(mContext).inflate(R.layout.chat_room_recv, null);
                GifTextView Content_from=(GifTextView)mView.findViewById(R.id.From_Content);
                Content_from.setText(mData.get(Index).getContent());
                break;
            case chat_contrue.WeChatMessage.MessageType_To:
                mView=LayoutInflater.from(mContext).inflate(R.layout.chat_room_send, null);
                GifTextView Content_to=(GifTextView)mView.findViewById(R.id.To_Content);
                Content_to.setText(mData.get(Index).getContent());
                break;
                default:
                    /*Toast.makeText(this,"fdfffffffffffffffff",Toast.LENGTH_LONG).show();*/
                    break;
        }
        return mView;
    }


}
