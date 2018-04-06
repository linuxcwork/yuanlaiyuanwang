package com.example.yang.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.example.yang.myapplication.Icon;
import com.example.yang.myapplication.R;

import java.util.ArrayList;

/**
 * Created by yang on 2018/3/31.
 */

public class installed_fragment extends Fragment {

    private BaseAdapter mAdapter = null;
    private ArrayList<Icon> mData = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_lifemainxml_installed, container, false);
        GridView inl_list = (GridView) view.findViewById(R.id.life_installed);

        return view;
    }
    public void data(){
        mData = new ArrayList<Icon>();
        /*mData.add(new Icon(R.mipmap.iv_icon_1, "图标1"));
        mData.add(new Icon(R.mipmap.iv_icon_2, "图标2"));
        mData.add(new Icon(R.mipmap.iv_icon_3, "图标3"));
        mData.add(new Icon(R.mipmap.iv_icon_4, "图标4"));
        mData.add(new Icon(R.mipmap.iv_icon_5, "图标5"));*/
    }
}
